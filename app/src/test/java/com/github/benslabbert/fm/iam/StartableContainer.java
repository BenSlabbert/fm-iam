package com.github.benslabbert.fm.iam;

import org.testcontainers.containers.GenericContainer;

public interface StartableContainer {

  GenericContainer<?> getContainer();

  void start();

  void stop();
}
