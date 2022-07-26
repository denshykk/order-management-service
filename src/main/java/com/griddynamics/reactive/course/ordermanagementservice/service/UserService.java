package com.griddynamics.reactive.course.ordermanagementservice.service;

import java.util.Optional;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.griddynamics.reactive.course.ordermanagementservice.adapter.OrderSearchAdapter;
import com.griddynamics.reactive.course.ordermanagementservice.adapter.ProductInfoAdapter;
import com.griddynamics.reactive.course.ordermanagementservice.domain.Order;
import com.griddynamics.reactive.course.ordermanagementservice.domain.OrderInfo;
import com.griddynamics.reactive.course.ordermanagementservice.domain.Product;
import com.griddynamics.reactive.course.ordermanagementservice.domain.User;
import com.griddynamics.reactive.course.ordermanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import static com.griddynamics.reactive.course.ordermanagementservice.utils.LogUtils.REQUEST_ID;
import static com.griddynamics.reactive.course.ordermanagementservice.utils.LogUtils.logOnError;
import static com.griddynamics.reactive.course.ordermanagementservice.utils.LogUtils.logOnNext;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository     userRepository;
  private final OrderSearchAdapter orderSearchAdapter;
  private final ProductInfoAdapter productInfoAdapter;

  public Flux<OrderInfo> getOrderInfoById(final String requestId, final String id) {
    return getUserById(requestId, id)
        .flatMapMany(user -> getOrdersByPhone(requestId, user.getPhone())
            .flatMap(order -> getProductByProductCode(requestId, order.getProductCode())
        .reduce(getProductWithHighestScore())
        .map(product -> toOrderInfo(user, order, product))
        .switchIfEmpty(Mono.just(toOrderInfo(user, order, null)))));
  }

  private Mono<User> getUserById(final String requestId, final String id) {
    return userRepository
        .findById(id)
        .doOnEach(logOnNext(user -> log.info("Info about user: {}", user)))
        .doOnEach(logOnError(error -> log.error("Error occurred while fetching data from MongoDB service", error)))
        .contextWrite(Context.of(REQUEST_ID, requestId));
  }

  private Flux<Order> getOrdersByPhone(final String requestId, final String phone) {
    return orderSearchAdapter
        .getOrdersByPhone(phone)
        .doOnEach(logOnNext(order -> log.info("Info about order: {}", order)))
        .contextWrite(Context.of(REQUEST_ID, requestId));
  }

  private Flux<Product> getProductByProductCode(final String requestId, final String productCode) {
    return productInfoAdapter
        .getProductNamesByProductCode(productCode)
        .doOnEach(logOnNext(product -> log.info("Info about product: {}", product)))
        .contextWrite(Context.of(REQUEST_ID, requestId));
  }

  private BiFunction<Product, Product, Product> getProductWithHighestScore() {
    return (product, product2) -> product.getScore() > product2.getScore() ? product : product2;
  }

  private OrderInfo toOrderInfo(final User user, final Order order, final Product product) {
    final OrderInfo orderInfo = OrderInfo
        .builder()
        .orderNumber(order.getOrderNumber())
        .userName(user.getName())
        .phoneNumber(user.getPhone())
        .productCode(order.getProductCode())
        .build();

    Optional.ofNullable(product).ifPresent(it -> {
      orderInfo.setProductName(product.getProductName());
      orderInfo.setProductId(product.getProductId());
    });

    return orderInfo;
  }

}
