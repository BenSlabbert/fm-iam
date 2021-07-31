package com.github.benslabbert.fm.iam;

import io.micronaut.runtime.Micronaut;

public class Application {

  public static void main(String[] args) {
    Micronaut.build(args).mainClass(Application.class).start();
  }
}
