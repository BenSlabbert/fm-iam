package com.github.benslabbert.fm.iam.service;

import com.github.benslabbert.fm.iam.config.RedisCacheConfig;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

@Slf4j
@Singleton
public class CacheService {

  public static final String REFRESH_PREFIX = "refresh-";

  private final JedisPool pool;

  public CacheService(RedisCacheConfig redisCacheConfig) {
    var poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(redisCacheConfig.getPoolMaxSize());
    poolConfig.setMaxIdle(redisCacheConfig.getPoolIdleSize());
    this.pool =
        new JedisPool(
            new JedisPoolConfig(),
            redisCacheConfig.getHost(),
            redisCacheConfig.getPort(),
            redisCacheConfig.getConnectionTimeout());
  }

  @PreDestroy
  public void destroy() {
    log.info("destroying cache...");
    pool.close();
    log.info("destroying cache...destroyed");
  }

  public void put(String key, byte[] value) {
    try (var jedis = pool.getResource()) {
      jedis.set(key.getBytes(StandardCharsets.UTF_8), value, SetParams.setParams().ex(10L));
    }
  }

  public Optional<byte[]> get(String key) {
    try (var jedis = pool.getResource()) {
      var bytes = jedis.get(key.getBytes(StandardCharsets.UTF_8));

      if (bytes == null) {
        return Optional.empty();
      }

      return Optional.of(bytes);
    }
  }

  public void remove(String key) {
    try (var jedis = pool.getResource()) {
      jedis.del(key);
    }
  }
}
