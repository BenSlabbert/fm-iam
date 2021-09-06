package com.github.benslabbert.fm.iam.grpc;

import com.github.benslabbert.fm.iam.interceptor.CustomInterceptor;
import com.github.benslabbert.fm.iam.proto.service.v1.CreateAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.CreateAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.DeleteAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.DeleteAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.IamServiceGrpc;
import com.github.benslabbert.fm.iam.proto.service.v1.LockAccountRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LockAccountResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LoginResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.LogoutRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.LogoutResponse;
import com.github.benslabbert.fm.iam.proto.service.v1.RefreshRequest;
import com.github.benslabbert.fm.iam.proto.service.v1.RefreshResponse;
import com.github.benslabbert.fm.iam.service.UserService;
import io.grpc.stub.StreamObserver;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
@ExecuteOn(TaskExecutors.IO)
public class IamService extends IamServiceGrpc.IamServiceImplBase {

  private final UserService userService;

  @Override
  public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
    var ctx = CustomInterceptor.CONTEXT_KEY.get();
    var reply = userService.login(ctx, request);
    complete(responseObserver, reply);
  }

  @Override
  public void refresh(RefreshRequest request, StreamObserver<RefreshResponse> responseObserver) {
    var ctx = CustomInterceptor.CONTEXT_KEY.get();
    var reply = userService.refresh(ctx, request);
    complete(responseObserver, reply);
  }

  @Override
  public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseObserver) {
    var ctx = CustomInterceptor.CONTEXT_KEY.get();
    var reply = userService.logout(ctx, request);
    complete(responseObserver, reply);
  }

  @Override
  public void createAccount(
      CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
    var ctx = CustomInterceptor.CONTEXT_KEY.get();
    var resp = userService.createAccount(ctx, request);
    complete(responseObserver, resp);
  }

  @Override
  public void deleteAccount(
      DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
    var ctx = CustomInterceptor.CONTEXT_KEY.get();
    var resp = userService.deleteAccount(ctx, request);
    complete(responseObserver, resp);
  }

  @Override
  public void lockAccount(
      LockAccountRequest request, StreamObserver<LockAccountResponse> responseObserver) {
    var ctx = CustomInterceptor.CONTEXT_KEY.get();
    var resp = userService.lockAccount(ctx, request);
    complete(responseObserver, resp);
  }

  private <T> void complete(StreamObserver<T> responseObserver, T msg) {
    responseObserver.onNext(msg);
    responseObserver.onCompleted();
  }
}
