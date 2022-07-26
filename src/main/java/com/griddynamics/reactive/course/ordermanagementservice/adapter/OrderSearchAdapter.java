package com.griddynamics.reactive.course.ordermanagementservice.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.griddynamics.reactive.course.ordermanagementservice.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import static com.griddynamics.reactive.course.ordermanagementservice.utils.LogUtils.logOnError;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSearchAdapter {

  public static final String GET_ORDER_BY_PHONE_ENDPOINT = "/order/phone";

  private final WebClient orderSearchWebClient;

  public Flux<Order> getOrdersByPhone(String phoneNumber) {
    var uri = UriComponentsBuilder
        .fromUriString(GET_ORDER_BY_PHONE_ENDPOINT)
        .queryParam("phoneNumber", phoneNumber)
        .buildAndExpand()
        .toUriString();

    return orderSearchWebClient
        .get()
        .uri(uri)
        .retrieve()
        .bodyToFlux(Order.class)
        .doOnEach(logOnError(error -> log.error("Error occurred while fetching data from OrderSearch service", error)))
        .switchIfEmpty(Flux.empty());
  }

}
