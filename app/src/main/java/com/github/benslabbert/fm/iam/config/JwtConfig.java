package com.github.benslabbert.fm.iam.config;

import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Singleton
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig {

  @Value("${jwt.secret:secretsecretsecretsecretsecretsecretsecretsecret}")
  private String secret;

  @Value("${jwt.token.lifetime:30}")
  private long tokenLifetimeSeconds;

  @Value("${jwt.refresh_token.lifetime:300}")
  private long refreshTokenLifetimeSeconds;

  public static JwtConfig ofDefaults() {
    return new JwtConfig("secretsecretsecretsecretsecretsecretsecretsecret", 30L, 300L);
  }
}
