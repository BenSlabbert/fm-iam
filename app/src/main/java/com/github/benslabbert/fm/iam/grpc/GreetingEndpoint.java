package com.github.benslabbert.fm.iam.grpc;

import com.github.benslabbert.fm.iam.proto.service.v1.IamServiceGrpc;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginResponse;
import com.github.benslabbert.fm.iam.service.UserService;
import io.grpc.stub.StreamObserver;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
@ExecuteOn(TaskExecutors.IO)
public class GreetingEndpoint extends IamServiceGrpc.IamServiceImplBase {

  private final UserService userService;

  @Override
  public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
    var reply = userService.login(request);
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
