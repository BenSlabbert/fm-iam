package com.github.benslabbert.fm.iam.service;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.KeyLengthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenServiceTest {

  private TokenService service;

  @BeforeEach
  void before() throws KeyLengthException {
    service = new TokenService();
  }

  @Test
  void token() {
    var token = service.create("id");
    var t = service.verify(token);
    assertTrue(t.isPresent());
    assertEquals("id", t.get().getUserId());

    var deserialize = service.deserialize(token);
    assertNotNull(deserialize);
    assertEquals(t.get().getUserId(), deserialize.getUserId());
  }

  @Test
  void refresh() {
    var token = service.createRefresh("id");
    var isValid = service.verifyRefresh(token);
    assertTrue(isValid);
  }

  @Test
  void getUserIdFromRefreshToken() {
    var token = service.createRefresh("id");
    var userId = service.getUserIdFromRefreshToken(token);
    assertEquals("id", userId);
  }
}
