package com.griddynamics.reactive.course.ordermanagementservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.griddynamics.reactive.course.ordermanagementservice.adapter.OrderSearchAdapter;
import com.griddynamics.reactive.course.ordermanagementservice.adapter.ProductInfoAdapter;
import com.griddynamics.reactive.course.ordermanagementservice.domain.Order;
import com.griddynamics.reactive.course.ordermanagementservice.domain.OrderInfo;
import com.griddynamics.reactive.course.ordermanagementservice.domain.Product;
import com.griddynamics.reactive.course.ordermanagementservice.domain.User;
import com.griddynamics.reactive.course.ordermanagementservice.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
  private static final Double SCORE        = 1689.37;

  @Mock
  private UserRepository     userRepository;
  @Mock
  private OrderSearchAdapter orderSearchAdapter;
  @Mock
  private ProductInfoAdapter productInfoAdapter;
  @InjectMocks
  private UserService        userService;

  private User  user;
  private Order order;

  @BeforeEach
  void setUp() {
    user = User.builder().id(USER_ID).name(USER_NAME).phone(USER_PHONE).build();
    order = Order.builder().phoneNumber(USER_PHONE).orderNumber(ORDER_NUMBER).productCode(PRODUCT_CODE).build();
  }

  @Test
  void shouldGetOrderInfoById() {
    final Product product = Product
        .builder()
        .productId(PRODUCT_ID)
        .productCode(PRODUCT_CODE)
        .productName(PRODUCT_NAME)
        .score(SCORE)
        .build();

    when(userRepository.findById(USER_ID)).thenReturn(Mono.just(user));
    when(orderSearchAdapter.getOrdersByPhone(USER_PHONE)).thenReturn(Flux.just(order));
    when(productInfoAdapter.getProductNamesByProductCode(PRODUCT_CODE)).thenReturn(Flux.just(product));

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
