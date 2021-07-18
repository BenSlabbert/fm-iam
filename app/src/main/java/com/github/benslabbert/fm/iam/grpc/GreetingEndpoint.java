package com.github.benslabbert.fm.iam.grpc;

import com.github.benslabbert.fm.iam.IamReply;
import com.github.benslabbert.fm.iam.IamRequest;
import com.github.benslabbert.fm.iam.IamServiceGrpc;
import com.github.benslabbert.fm.iam.service.GreetService;
import io.grpc.stub.StreamObserver;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class GreetingEndpoint extends IamServiceGrpc.IamServiceImplBase {

  private final GreetService greetService;

  @Override
  public void send(IamRequest request, StreamObserver<IamReply> responseObserver) {
    log.info("handle send");
    var reply = greetService.getReply(request);
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
