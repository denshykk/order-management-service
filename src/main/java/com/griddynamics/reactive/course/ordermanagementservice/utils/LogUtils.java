package com.griddynamics.reactive.course.ordermanagementservice.utils;

import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.MDC;

import reactor.core.publisher.Signal;

public class LogUtils {

  public static final String REQUEST_ID = "requestId";

  private LogUtils() {
  }

  public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
    return signal -> {
      if (!signal.isOnNext()) {
        return;
      }
      Optional<String> toPutInMdc = signal.getContextView().getOrEmpty(REQUEST_ID);

      toPutInMdc.ifPresentOrElse(tpim -> {
        try (MDC.MDCCloseable cMdc = MDC.putCloseable("MDC_KEY", tpim)) {
          logStatement.accept(signal.get());
        }
      }, () -> logStatement.accept(signal.get()));
    };
  }

  public static <T> Consumer<Signal<T>> logOnError(Consumer<Throwable> logStatement) {
    return signal -> {
      if (!signal.isOnError()) {
        return;
      }
      Optional<String> toPutInMdc = signal.getContextView().getOrEmpty(REQUEST_ID);

      toPutInMdc.ifPresentOrElse(tpim -> {
        try (MDC.MDCCloseable cMdc = MDC.putCloseable("MDC_KEY", tpim)) {
          logStatement.accept(signal.getThrowable());
        }
      }, () -> logStatement.accept(signal.getThrowable()));
    };
  }

}
