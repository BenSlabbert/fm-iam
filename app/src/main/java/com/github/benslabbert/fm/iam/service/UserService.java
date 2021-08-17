package com.github.benslabbert.fm.iam.service;

import com.github.benslabbert.fm.iam.interceptor.RequestContext;
import com.github.benslabbert.fm.iam.proto.service.v1.CreateAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.CreateAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.DeleteAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LockAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.LogoutRequest;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UserService {

  // todo we need to add database stuff here too

  public LoginResponse login(RequestContext ctx, LoginRequest request) {
    var username = request.getName();
    var password = request.getPassword();
    log.info("username {}, password {}", username, password);
    return LoginResponse.newBuilder().setToken("new token").build();
  }

  public void logout(RequestContext ctx, LogoutRequest request) {}

  public CreateAccountResponse createAccount(RequestContext ctx, CreateAccountRequest request) {
    return CreateAccountResponse.getDefaultInstance();
  }

  public void deleteAccount(RequestContext ctx, DeleteAccountRequest request) {}

  public void lockAccount(RequestContext ctx, LockAccountRequest request) {}
}
