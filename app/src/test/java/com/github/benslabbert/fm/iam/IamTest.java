package com.github.benslabbert.fm.iam;

import static org.junit.jupiter.api.Assertions.*;

import io.grpc.ManagedChannelBuilder;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
class IamTest {

  @Inject EmbeddedApplication<?> application;

  @Test
  void testItWorks() {
    assertTrue(application.isRunning());

    var channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
    var request =
        com.github.benslabbert.fm.iam.proto.IamRequest.newBuilder().setName("test").build();
    var reply =
        com.github.benslabbert.fm.iam.proto.IamServiceGrpc.newBlockingStub(channel).send(request);

    assertNotNull(reply);
    assertNotNull(reply.getMessage());
  }
}
