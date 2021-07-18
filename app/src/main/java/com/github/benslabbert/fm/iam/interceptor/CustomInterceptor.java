package com.github.benslabbert.fm.iam.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.micronaut.core.order.Ordered;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CustomInterceptor implements ServerInterceptor, Ordered {

  @Override
  public <R, T> ServerCall.Listener<R> interceptCall(
      ServerCall<R, T> call, Metadata headers, ServerCallHandler<R, T> next) {
    if (log.isTraceEnabled()) {
      log.trace("simple interceptor");
    }
    return next.startCall(call, headers);
  }

  @Override
  public int getOrder() {
    return 10;
  }
}
