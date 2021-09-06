package com.github.benslabbert.fm.iam;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;

public class TestApplicationWrapper {

  private final EmbeddedServer embeddedServer;
  private final ApplicationContext applicationContext;
  private final int grpcPort;

  public TestApplicationWrapper(
      EmbeddedServer embeddedServer, ApplicationContext applicationContext, int grpcPort) {
    this.embeddedServer = embeddedServer;
    this.applicationContext = applicationContext;
    this.grpcPort = grpcPort;
  }

  public EmbeddedServer getEmbeddedServer() {
    return embeddedServer;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public int getGrpcPort() {
    return grpcPort;
  }
}
