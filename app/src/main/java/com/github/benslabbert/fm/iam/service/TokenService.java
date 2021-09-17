package com.github.benslabbert.fm.iam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private static final byte[] SECRET =
      "secretsecretsecretsecretsecretsecretsecretsecret".getBytes(StandardCharsets.UTF_8);

  private static final long TOKEN_LIFETIME_SECONDS = 30L;
  private static final long REFRESH_TOKEN_LIFETIME_SECONDS = 300L;
  private static final String USER_CLAIM = "x-user";
  private static final String REFRESH_CLAIM = "x-refresh";

  private final JWSSigner signer;

  public TokenService() throws KeyLengthException {
    signer = new MACSigner(SECRET);
  }

  @SneakyThrows
  public String create(String userId) {
    var payload = new ObjectMapper().writeValueAsString(Token.builder().userId(userId).build());
    var claims = createClaim(USER_CLAIM, payload, TOKEN_LIFETIME_SECONDS);

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
    var verifier = new MACVerifier(SECRET);

    if (!obj.verify(verifier)) {
      return Optional.empty();
    }

    var mapper = new ObjectMapper();
    var userClaim = obj.getJWTClaimsSet().getStringClaim(USER_CLAIM);
    return Optional.of(mapper.readValue(userClaim, new TypeReference<>() {}));
  }

  @SneakyThrows
  public String createRefresh(String userId) {
    var claims = createClaim(REFRESH_CLAIM, userId, REFRESH_TOKEN_LIFETIME_SECONDS);
    var signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  @SneakyThrows
  public boolean isValid(String token) {
    var obj = SignedJWT.parse(token);
    var verifier = new MACVerifier(SECRET);
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
