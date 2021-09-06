package com.github.benslabbert.fm.iam;

import static org.assertj.core.api.Assertions.*;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ApplicationTestServices {

  protected static PsqlContainer newPsql() {
    return new PsqlContainer(
        new GenericContainer<>("postgres:13-alpine")
            .withEnv("POSTGRES_USER", "user")
            .withEnv("POSTGRES_PASSWORD", "password")
            .withEnv("POSTGRES_DB", "db")
            .withExposedPorts(5432)
            .waitingFor(Wait.forListeningPort()));
  }

  protected static RedisContainer newRedis() {
    return new RedisContainer(
        new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort()));
  }

  protected static TestApplicationWrapper startApplication(
      PsqlContainer psqlContainer, RedisContainer redisContainer) {
    assertThat(psqlContainer.getContainer().isRunning()).isTrue();
    assertThat(redisContainer.getContainer().isRunning()).isTrue();

    int grpcPort = ThreadLocalRandom.current().nextInt(50000, 55000);

    var embeddedServer =
        ApplicationContext.run(
            EmbeddedServer.class,
            Map.of(
                "datasources.default.url",
                "jdbc:postgresql://localhost:"
                    + psqlContainer.getContainer().getMappedPort(5432)
                    + "/db",
                "cache.redis.port",
                redisContainer.getContainer().getMappedPort(6379),
                "grpc.server.port",
                grpcPort));

    var applicationContext = embeddedServer.getApplicationContext();
    return new TestApplicationWrapper(embeddedServer, applicationContext, grpcPort);
  }
}
