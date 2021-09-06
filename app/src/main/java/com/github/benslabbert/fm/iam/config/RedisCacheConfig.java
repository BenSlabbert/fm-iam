package com.github.benslabbert.fm.iam.config;

import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import lombok.Getter;

@Getter
@Singleton
public class RedisCacheConfig {

  @Value("${cache.redis.host:localhost}")
  private String host;

  @Value("${cache.redis.port:6379}")
  private Integer port;

  @Value("${cache.redis.connectionTimeout:500}")
  private Integer connectionTimeout;

  @Value("${cache.redis.pool.maxSize:4}")
  private Integer poolMaxSize;

  @Value("${cache.redis.pool.idleSize:2}")
  private Integer poolIdleSize;
}
