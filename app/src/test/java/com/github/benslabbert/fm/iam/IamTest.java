package com.github.benslabbert.fm.iam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class IamTest extends ApplicationTestServices {

  private static TestApplicationWrapper testApplicationWrapper;

  private static final PsqlContainer PSQL = newPsql();
  private static final RedisContainer REDIS = newRedis();

  @BeforeAll
  static void beforeAll() {
    Stream.of(PSQL, REDIS).parallel().forEach(StartableContainer::start);
    testApplicationWrapper = startApplication(PSQL, REDIS);
  }

  @AfterAll
  protected static void afterAll() {
    testApplicationWrapper.getApplicationContext().stop();
    Stream.of(PSQL, REDIS).parallel().forEach(StartableContainer::stop);
  }

  @Test
  void testItWorks() {
    assertTrue(testApplicationWrapper.getApplicationContext().isRunning());
  }
}
