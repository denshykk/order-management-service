package com.griddynamics.reactive.course.ordermanagementservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.griddynamics.reactive.course.ordermanagementservice.adapter.OrderSearchAdapter;
import com.griddynamics.reactive.course.ordermanagementservice.adapter.ProductInfoAdapter;
import com.griddynamics.reactive.course.ordermanagementservice.domain.OrderInfo;
import com.griddynamics.reactive.course.ordermanagementservice.domain.User;
import com.griddynamics.reactive.course.ordermanagementservice.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private static final String USER_ID      = "user1";
  private static final String REQUEST_ID   = "1234";
  private static final String USER_NAME    = "John";
  private static final String USER_PHONE   = "123456789";
  private static final String ORDER_NUMBER = "Order_0";
  private static final String PRODUCT_CODE = "3852";
  private static final String PRODUCT_ID   = "111";
  private static final String PRODUCT_NAME = "IceCream";

  private WireMockServer wireMockServer1 = new WireMockServer(8081);
  private WireMockServer wireMockServer2 = new WireMockServer(8082);

  @Mock
  private UserRepository     userRepository;
  @Autowired
  private OrderSearchAdapter orderSearchAdapter;
  @Autowired
  private ProductInfoAdapter productInfoAdapter;
  private UserService        userService;

  @BeforeEach
  void setUp() {
    wireMockServer1.start();
    wireMockServer2.start();

    userService = new UserService(userRepository, orderSearchAdapter, productInfoAdapter);
  }

  @AfterEach
  void tearDown() {
    wireMockServer1.stop();
    wireMockServer2.stop();
  }

  @Test
  void shouldGetOrderInfoById() {
    User user = User.builder().id(USER_ID).name(USER_NAME).phone(USER_PHONE).build();
    when(userRepository.findById(USER_ID)).thenReturn(Mono.just(user));

    WireMock.configureFor(wireMockServer1.port());
    WireMock.stubFor(WireMock
        .get(WireMock.urlMatching("/orderSearchService/order/phone\\?phoneNumber=" + USER_PHONE))
        .willReturn(WireMock
            .aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody("[{\"orderNumber\":\"Order_0\",\"productCode\":\"3852\"}]")));

    WireMock.configureFor(wireMockServer2.port());
    WireMock.stubFor(WireMock
        .get(WireMock.urlMatching("/productInfoService/product/names\\?productCode=" + PRODUCT_CODE))
        .willReturn(WireMock
            .aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody("{\"productId\":\"111\",\"productCode\":\"3852\",\"productName\":\"IceCream\",\"score\":1689" +
                ".37}")));

    final Flux<OrderInfo> orderInfoFlux = userService.getOrderInfoById(REQUEST_ID, USER_ID);

    StepVerifier.create(orderInfoFlux).assertNext(orderInfo -> {
      assertThat(orderInfo.getOrderNumber()).isEqualTo(ORDER_NUMBER);
      assertThat(orderInfo.getUserName()).isEqualTo(USER_NAME);
      assertThat(orderInfo.getPhoneNumber()).isEqualTo(USER_PHONE);
      assertThat(orderInfo.getProductCode()).isEqualTo(PRODUCT_CODE);
      assertThat(orderInfo.getProductName()).isEqualTo(PRODUCT_NAME);
      assertThat(orderInfo.getProductId()).isEqualTo(PRODUCT_ID);
    }).verifyComplete();
  }

}
