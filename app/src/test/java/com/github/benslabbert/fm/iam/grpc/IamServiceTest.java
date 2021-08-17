package com.github.benslabbert.fm.iam.grpc;

import com.github.benslabbert.fm.iam.proto.service.v1.IamServiceGrpc;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginResponse;
import io.grpc.ManagedChannelBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class IamServiceTest {

  @Test
  void test() {
    var channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
    var blockingStub = IamServiceGrpc.newBlockingStub(channel);
    LoginResponse loginResponse =
        blockingStub.login(LoginRequest.newBuilder().setName("").setPassword("").build());
    Assertions.assertThat(loginResponse).isNotNull();
  }
}
