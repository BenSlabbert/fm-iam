package com.github.benslabbert.fm.iam.service;

import static org.junit.jupiter.api.Assertions.*;

import com.github.benslabbert.fm.iam.config.JwtConfig;
import com.nimbusds.jose.KeyLengthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenServiceTest {

  private TokenService service;

  @BeforeEach
  void before() throws KeyLengthException {
    service = new TokenService(JwtConfig.ofDefaults());
  }

  @Test
  void token() {
    var token = service.create("id");
    var t = service.parse(token);
    assertTrue(t.isPresent());
    assertEquals("id", t.get().getUserId());

    var deserialize = service.deserialize(token);
    assertNotNull(deserialize);
    assertEquals(t.get().getUserId(), deserialize.getUserId());
  }

  @Test
  void refresh() {
    var token = service.createRefresh("id");
    var isValid = service.isValid(token);
    assertTrue(isValid);
  }

  @Test
  void getUserIdFromRefreshToken() {
    var token = service.createRefresh("id");
    var userId = service.getUserIdFromRefreshToken(token);
    assertEquals("id", userId);
  }
}
