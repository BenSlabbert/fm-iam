package com.github.benslabbert.fm.iam.service;

import com.github.benslabbert.fm.iam.IamReply;
import com.github.benslabbert.fm.iam.IamRequest;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class GreetService {

  public IamReply getReply(IamRequest request) {
    log.info("request.name {}", request.getName());
    return IamReply.newBuilder().setMessage("hello").build();
  }
}
