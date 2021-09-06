package com.github.benslabbert.fm.iam;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer implements StartableContainer {

  private final GenericContainer<?> container;

  public RedisContainer(GenericContainer<?> container) {
    this.container = container;
  }

  @Override
  public GenericContainer<?> getContainer() {
    return container;
  }

  @Override
  public void start() {
    container.start();
  }

  @Override
  public void stop() {
    container.stop();
  }
}
