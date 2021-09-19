package com.github.benslabbert.fm.iam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benslabbert.fm.iam.config.JwtConfig;
import com.github.benslabbert.fm.iam.dto.Token;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class TokenService {

  private final byte[] secret;
  private final long tokenLifetimeSeconds;
  private final long refreshTokenLifetimeSeconds;
  private static final String USER_CLAIM = "x-user";
  private static final String REFRESH_CLAIM = "x-refresh";

  private final JWSSigner signer;

  public TokenService(JwtConfig jwtConfig) throws KeyLengthException {
    secret = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
    tokenLifetimeSeconds = jwtConfig.getTokenLifetimeSeconds();
    refreshTokenLifetimeSeconds = jwtConfig.getRefreshTokenLifetimeSeconds();
    signer = new MACSigner(secret);
  }

  @SneakyThrows
  public String create(String userId) {
    var payload = new ObjectMapper().writeValueAsString(Token.builder().userId(userId).build());
    var claims = createClaim(USER_CLAIM, payload, tokenLifetimeSeconds);

    var signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  @SneakyThrows
  public Token deserialize(String token) {
    var obj = SignedJWT.parse(token);
    var mapper = new ObjectMapper();
    var userClaim = obj.getJWTClaimsSet().getStringClaim(USER_CLAIM);
    return mapper.readValue(userClaim, new TypeReference<>() {});
  }

  @SneakyThrows
  public Optional<Token> parse(String token) {
    var obj = SignedJWT.parse(token);
    var verifier = new MACVerifier(secret);

    if (!obj.verify(verifier)) {
      return Optional.empty();
    }

    var mapper = new ObjectMapper();
    var userClaim = obj.getJWTClaimsSet().getStringClaim(USER_CLAIM);
    return Optional.of(mapper.readValue(userClaim, new TypeReference<>() {}));
  }

  @SneakyThrows
  public String createRefresh(String userId) {
    var claims = createClaim(REFRESH_CLAIM, userId, refreshTokenLifetimeSeconds);
    var signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  @SneakyThrows
  public boolean isValid(String token) {
    var obj = SignedJWT.parse(token);
    var verifier = new MACVerifier(secret);
    return obj.verify(verifier);
  }

  @SneakyThrows
  public boolean isExpired(String token) {
    var obj = SignedJWT.parse(token);
    return obj.getJWTClaimsSet().getExpirationTime().before(Date.from(Instant.now()));
  }

  @SneakyThrows
  public String getUserIdFromRefreshToken(String refreshToken) {
    var obj = SignedJWT.parse(refreshToken);
    return obj.getJWTClaimsSet().getStringClaim(REFRESH_CLAIM);
  }

  private JWTClaimsSet createClaim(String claim, String payload, long expiration) {
    return new JWTClaimsSet.Builder()
        .notBeforeTime(Date.from(Instant.now()))
        .expirationTime(Date.from(Instant.now().plusSeconds(expiration)))
        .claim(claim, payload)
        .build();
  }
}
