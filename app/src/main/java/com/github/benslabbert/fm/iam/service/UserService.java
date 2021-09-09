package com.github.benslabbert.fm.iam.service;

import com.github.benslabbert.fm.iam.dao.entity.User;
import com.github.benslabbert.fm.iam.dao.repo.UserRepo;
import com.github.benslabbert.fm.iam.exception.BadCredentialsException;
import com.github.benslabbert.fm.iam.exception.NotFoundException;
import com.github.benslabbert.fm.iam.exception.SessionExpiredException;
import com.github.benslabbert.fm.iam.interceptor.RequestContext;
import com.github.benslabbert.fm.iam.proto.common.UUID;
import com.github.benslabbert.fm.iam.proto.message.v1.UserMessage;
import com.github.benslabbert.fm.iam.proto.service.v1.CreateAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.CreateAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.DeleteAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.DeleteAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.LockAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LockAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.LogoutRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LogoutResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.RefreshRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.RefreshResponse;
import io.micronaut.core.util.StringUtils;
import java.nio.charset.StandardCharsets;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class UserService {

  private final PasswordService passwordService;
  private final CacheService cacheService;
  private final TokenService tokenService;
  private final UserRepo userRepo;

  @Transactional
  public LoginResponse login(RequestContext ctx, LoginRequest request) {
    log.trace("{} login user {}", ctx.requestId, request.getName());

    var user = userRepo.findByNameEqualsAndLockedFalseAndDeletedFalse(request.getName());
    if (user.isEmpty()) {
      throw new BadCredentialsException();
    }

    var valid =
        passwordService.checkPassword(
            request.getPassword().toStringUtf8().toCharArray(),
            user.get().getPasswordHash(),
            user.get().getPasswordSalt());

    if (!valid) {
      throw new BadCredentialsException();
    }

    var refreshToken = createNewRefreshToken(user.get().getId().toString());
    return LoginResponse.newBuilder()
        .setToken(tokenService.create(user.get().getId().toString()))
        .setRefreshToken(refreshToken)
        .build();
  }

  public RefreshResponse refresh(RequestContext ctx, RefreshRequest request) {
    if (StringUtils.isEmpty(request.getRefreshToken())) {
      throw new SessionExpiredException();
    }

    var userId = tokenService.getUserIdFromRefreshToken(request.getRefreshToken());
    var refreshToken = cacheService.get(CacheService.REFRESH_PREFIX + userId);

    if (refreshToken.isEmpty()) {
      log.trace("{} no token found", ctx.requestId);
      throw new SessionExpiredException();
    }

    var token = new String(refreshToken.get());
    if (!tokenService.verifyRefresh(token)) {
      log.trace("{} invalid token", ctx.requestId);
      throw new SessionExpiredException();
    }

    if (tokenService.isExpired(token)) {
      log.trace("{} token expired", ctx.requestId);
      throw new SessionExpiredException();
    }

    if (!token.equals(request.getRefreshToken())) {
      log.trace("{} incorrect refresh token provided", ctx.requestId);
      throw new SessionExpiredException();
    }

    var newToken = tokenService.create(userId);
    var newRefreshToken = createNewRefreshToken(userId);
    return RefreshResponse.newBuilder().setToken(newToken).setRefreshToken(newRefreshToken).build();
  }

  public LogoutResponse logout(RequestContext ctx, LogoutRequest request) {
    log.trace("{} logout user {}", ctx.requestId, request.getToken());

    var token = tokenService.deserialize(request.getToken());
    cacheService.remove(CacheService.REFRESH_PREFIX + token.getUserId());
    return LogoutResponse.getDefaultInstance();
  }

  @Transactional
  public CreateAccountResponse createAccount(RequestContext ctx, CreateAccountRequest request) {
    log.trace("{} create user {}", ctx.requestId, request.getName());

    var hashes = passwordService.generateHashes(request.getPassword().toStringUtf8().toCharArray());

    var user =
        userRepo.save(
            User.builder()
                .name(request.getName())
                .passwordHash(hashes.getPasswordHash())
                .passwordSalt(hashes.getSalt())
                .locked(false)
                .deleted(false)
                .build());

    log.trace("{} created user with id {}", ctx.requestId, user.getId());

    return CreateAccountResponse.newBuilder()
        .setUser(
            UserMessage.newBuilder()
                .setId(UUID.newBuilder().setValue(user.getId().toString()).build())
                .setName(request.getName())
                .build())
        .build();
  }

  @Transactional
  public DeleteAccountResponse deleteAccount(RequestContext ctx, DeleteAccountRequest request) {
    log.trace("{} delete user {}", ctx.requestId, request);

    var user = userRepo.findByNameEquals(request.getName());
    if (user.isEmpty()) {
      throw new NotFoundException();
    }

    user.get().setDeleted(true);
    userRepo.save(user.get());

    return DeleteAccountResponse.getDefaultInstance();
  }

  @Transactional
  public LockAccountResponse lockAccount(RequestContext ctx, LockAccountRequest request) {
    log.trace("{} lock user {}", ctx.requestId, request);

    var user = userRepo.findByNameEquals(request.getName());
    if (user.isEmpty()) {
      throw new NotFoundException();
    }

    if (Boolean.TRUE.equals(user.get().getDeleted())) {
      // noop
      return LockAccountResponse.getDefaultInstance();
    }

    user.get().setLocked(true);
    userRepo.save(user.get());
    return LockAccountResponse.getDefaultInstance();
  }

  private String createNewRefreshToken(String userId) {
    var newToken = tokenService.createRefresh(userId);
    cacheService.put(
        CacheService.REFRESH_PREFIX + userId, newToken.getBytes(StandardCharsets.UTF_8));
    return newToken;
  }
}
