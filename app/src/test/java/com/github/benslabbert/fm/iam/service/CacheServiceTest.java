package com.github.benslabbert.fm.iam.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benslabbert.fm.iam.ApplicationTestServices;
import com.github.benslabbert.fm.iam.PsqlContainer;
import com.github.benslabbert.fm.iam.RedisContainer;
import com.github.benslabbert.fm.iam.StartableContainer;
import com.github.benslabbert.fm.iam.TestApplicationWrapper;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CacheServiceTest extends ApplicationTestServices {

  private static CacheService service;
  private static TestApplicationWrapper testApplicationWrapper;

  private static final PsqlContainer PSQL = newPsql();
  private static final RedisContainer REDIS = newRedis();

  @BeforeAll
  static void beforeAll() {
    Stream.of(PSQL, REDIS).parallel().forEach(StartableContainer::start);

    testApplicationWrapper = startApplication(PSQL, REDIS);
    service = testApplicationWrapper.getApplicationContext().getBean(CacheService.class);
  }

  @AfterAll
  protected static void afterAll() {
    testApplicationWrapper.getApplicationContext().stop();
    Stream.of(PSQL, REDIS).parallel().forEach(StartableContainer::stop);
  }

  @Test
  void test() {
    service.put("key", "value".getBytes(StandardCharsets.UTF_8));

    var obj = service.get("key");
    assertThat(obj).isPresent();
    assertThat(new String(obj.get(), StandardCharsets.UTF_8)).isEqualTo("value");

    service.remove("key");
    obj = service.get("key");
    assertThat(obj).isEmpty();
  }
}
