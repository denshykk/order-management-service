package com.griddynamics.reactive.course.ordermanagementservice.adapter;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.griddynamics.reactive.course.ordermanagementservice.domain.Order;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSearchAdapterTest {

  private static final String PHONE_NUMBER   = "123456789";
  private static final String ORDER_NUMBER   = "Order_0";
  private static final String PRODUCT_CODE   = "3852";
  private static final String JSON_FILE_PATH =
      "/Users/dtykhonov/IdeaProjects/Reactive_Paradigm_FOR_STUDENTS/order-management-service" +
          "/src/test/resources/order-search-adapter-response.json";

  private OrderSearchAdapter orderSearchAdapter;
  private String             json;

  @BeforeEach
  @SneakyThrows
  void setUp() {
    json = Files.readAllLines(Path.of(JSON_FILE_PATH)).get(0);

    WebClient webClient = WebClient
        .builder()
        .exchangeFunction(request -> Mono.just(ClientResponse
            .create(HttpStatus.OK)
            .header("content-type", "application/json")
            .body(json)
            .build()))
        .build();

    orderSearchAdapter = new OrderSearchAdapter(webClient);
  }

  @Test
  void shouldGetOrdersByPhone() {
    final Flux<Order> orderFlux = orderSearchAdapter.getOrdersByPhone(PHONE_NUMBER);

    StepVerifier.create(orderFlux).assertNext(order -> {
      assertThat(order.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
      assertThat(order.getOrderNumber()).isEqualTo(ORDER_NUMBER);
      assertThat(order.getProductCode()).isEqualTo(PRODUCT_CODE);
    }).verifyComplete();
  }

}
