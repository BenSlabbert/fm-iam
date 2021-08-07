package com.github.benslabbert.fm.iam.service;

import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginResponse;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UserService {

  public LoginResponse login(LoginRequest request) {
    var username = request.getName();
    var password = request.getPassword();
    log.info("username {}, password {}", username, password);
    return LoginResponse.newBuilder().setToken("new token").build();
  }
}
