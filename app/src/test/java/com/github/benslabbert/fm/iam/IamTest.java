package com.github.benslabbert.fm.iam;

import io.grpc.ManagedChannelBuilder;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class IamTest {

  @Inject EmbeddedApplication<?> application;

  @Test
  void testItWorks() {
    Assertions.assertTrue(application.isRunning());

    var channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
    var request = com.github.benslabbert.fm.iam.IamRequest.newBuilder().setName("test").build();
    var reply = com.github.benslabbert.fm.iam.IamServiceGrpc.newBlockingStub(channel).send(request);

    Assertions.assertNotNull(reply);
    Assertions.assertNotNull(reply.getMessage());
  }
}
