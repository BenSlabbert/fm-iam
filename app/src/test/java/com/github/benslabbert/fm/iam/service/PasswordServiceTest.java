package com.github.benslabbert.fm.iam.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PasswordServiceTest {

  static Stream<Arguments> passwords() {
    return Stream.of(
        Arguments.of("password".toCharArray()),
        Arguments.of("@#%&{ASFPOashvafywgefkv".toCharArray()));
  }

  @MethodSource("passwords")
  @ParameterizedTest
  void test(char[] password) {
    var ps = new PasswordService();
    var hashes = ps.generateHashes(password);
    assertThat(ps.checkPassword(password, hashes.getPasswordHash(), hashes.getSalt())).isTrue();
  }
}
