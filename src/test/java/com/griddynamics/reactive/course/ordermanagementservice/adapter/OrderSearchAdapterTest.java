package com.griddynamics.reactive.course.ordermanagementservice.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.griddynamics.reactive.course.ordermanagementservice.domain.Order;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWireMock(port = 8081)
@ExtendWith(SpringExtension.class)
class OrderSearchAdapterTest {

  private static final String PHONE_NUMBER = "123456789";
  private static final String ORDER_NUMBER = "Order_0";
  private static final String PRODUCT_CODE = "3852";
  private static final String JSON         = "{\"phoneNumber\": \"123456789\", \"orderNumber\": \"Order_0\", " +
      "\"productCode\": \"3852\"}";

  @Autowired
  private OrderSearchAdapter orderSearchAdapter;

  @Test
  void shouldGetOrdersByPhone() {
    WireMock.stubFor(WireMock
        .get(WireMock.urlMatching("/orderSearchService/order/phone\\?phoneNumber=" + PHONE_NUMBER))
        .willReturn(WireMock.aResponse().withHeader("content-type", "application/json").withBody(JSON)));

    final Flux<Order> orderFlux = orderSearchAdapter.getOrdersByPhone(PHONE_NUMBER);

    StepVerifier.create(orderFlux).assertNext(order -> {
      assertThat(order.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
      assertThat(order.getOrderNumber()).isEqualTo(ORDER_NUMBER);
      assertThat(order.getProductCode()).isEqualTo(PRODUCT_CODE);
    }).verifyComplete();
  }

}
