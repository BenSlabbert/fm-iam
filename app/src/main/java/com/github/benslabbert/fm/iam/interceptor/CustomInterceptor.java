package com.github.benslabbert.fm.iam.interceptor;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.micronaut.core.order.Ordered;
import io.micronaut.core.util.StringUtils;
import java.util.UUID;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CustomInterceptor implements ServerInterceptor, Ordered {

  public static final Context.Key<RequestContext> CONTEXT_KEY =
      Context.keyWithDefault("ctx", new RequestContext("unknown request"));
  public static final Metadata.Key<String> TRACE_ID =
      Metadata.Key.of("trace_id", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <R, T> ServerCall.Listener<R> interceptCall(
      ServerCall<R, T> call, Metadata headers, ServerCallHandler<R, T> next) {
    if (log.isTraceEnabled()) {
      log.trace("simple interceptor");
    }

    var traceId = headers.get(TRACE_ID);
    if (StringUtils.isEmpty(traceId)) {
      traceId = UUID.randomUUID().toString();
    }

    var context = Context.current().withValue(CONTEXT_KEY, new RequestContext(traceId));
    return Contexts.interceptCall(context, call, headers, next);
  }

  @Override
  public int getOrder() {
    return 10;
  }
}
