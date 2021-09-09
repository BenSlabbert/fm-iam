package com.github.benslabbert.fm.iam.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benslabbert.fm.iam.ApplicationTestServices;
import com.github.benslabbert.fm.iam.PsqlContainer;
import com.github.benslabbert.fm.iam.RedisContainer;
import com.github.benslabbert.fm.iam.StartableContainer;
import com.github.benslabbert.fm.iam.TestApplicationWrapper;
import com.github.benslabbert.fm.iam.dao.repo.UserRepo;
import com.github.benslabbert.fm.iam.proto.service.v1.IamServiceGrpc;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LogoutRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.RefreshRequest;
import com.github.benslabbert.fm.iam.service.TokenService;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannelBuilder;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IamServiceTest extends ApplicationTestServices {

  private static final PsqlContainer PSQL = newPsql();
  private static final RedisContainer REDIS = newRedis();

  private static TestApplicationWrapper testApplicationWrapper;
  private static TokenService tokenService;
  private static UserRepo userRepo;

  private IamServiceGrpc.IamServiceBlockingStub stub;

  @BeforeAll
  static void beforeAll() {
    Stream.of(PSQL, REDIS).parallel().forEach(StartableContainer::start);

    testApplicationWrapper = startApplication(PSQL, REDIS);
    tokenService = testApplicationWrapper.getApplicationContext().getBean(TokenService.class);
    userRepo = testApplicationWrapper.getApplicationContext().getBean(UserRepo.class);
  }

  @AfterAll
  protected static void afterAll() {
    testApplicationWrapper.getApplicationContext().stop();
    Stream.of(PSQL, REDIS).parallel().forEach(StartableContainer::stop);
  }

  @BeforeEach
  void before() {
    var channel =
        ManagedChannelBuilder.forAddress("localhost", testApplicationWrapper.getGrpcPort())
            .usePlaintext()
            .build();

    stub = IamServiceGrpc.newBlockingStub(channel);
  }

  @Test
  void login() {
    var loginResponse =
        stub.login(
            LoginRequest.newBuilder()
                .setName("test")
                .setPassword(ByteString.copyFromUtf8("password"))
                .build());
    assertThat(loginResponse).isNotNull();

    var userId = tokenService.deserialize(loginResponse.getToken()).getUserId();
    assertThat(userId).isNotNull();

    var user = userRepo.findByNameEquals("test");
    assertThat(user).isPresent();
    assertThat(user.get().getId()).hasToString(userId);

    var refreshResponse =
        stub.refresh(
            RefreshRequest.newBuilder().setRefreshToken(loginResponse.getRefreshToken()).build());
    assertThat(refreshResponse).isNotNull();
    assertThat(refreshResponse.getToken()).isNotNull();
    assertThat(refreshResponse.getRefreshToken()).isNotNull();

    var logoutResponse =
        stub.logout(LogoutRequest.newBuilder().setToken(refreshResponse.getToken()).build());
    assertThat(logoutResponse).isNotNull();
  }
}
