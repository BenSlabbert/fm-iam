package com.github.benslabbert.fm.iam.service;

import com.github.benslabbert.fm.iam.exception.InternalServiceException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Singleton;
import lombok.SneakyThrows;

@Singleton
public class PasswordService {

  private static final int ITERATION_COUNT = 65536;
  private static final int KEY_LENGTH = 2048;
  private static final int SALT_LENGTH = 512;

  private final SecureRandom random;
  private final SecretKeyFactory secretKeyFactory;

  @SneakyThrows
  public PasswordService() {
    this.random = new SecureRandom();
    this.secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
  }

  public PasswordHashes generateHashes(char[] password) {
    try {
      var salt = new byte[SALT_LENGTH];
      random.nextBytes(salt);
      var spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
      var hash = secretKeyFactory.generateSecret(spec).getEncoded();
      return new PasswordHashes(hash, salt);
    } catch (InvalidKeySpecException e) {
      throw new InternalServiceException(e);
    }
  }

  public boolean checkPassword(char[] password, byte[] passwordHash, byte[] salt) {
    try {
      var spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
      var hash = secretKeyFactory.generateSecret(spec).getEncoded();
      return Arrays.equals(passwordHash, hash);
    } catch (InvalidKeySpecException e) {
      throw new InternalServiceException(e);
    }
  }

  static class PasswordHashes {

    private final byte[] passwordHash;
    private final byte[] salt;

    PasswordHashes(byte[] passwordHash, byte[] salt) {
      this.passwordHash = passwordHash;
      this.salt = salt;
    }

    public byte[] getPasswordHash() {
      return passwordHash;
    }

    public byte[] getSalt() {
      return salt;
    }
  }
}
