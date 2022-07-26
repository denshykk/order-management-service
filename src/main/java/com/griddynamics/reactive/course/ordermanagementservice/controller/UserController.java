package com.griddynamics.reactive.course.ordermanagementservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.griddynamics.reactive.course.ordermanagementservice.domain.OrderInfo;
import com.griddynamics.reactive.course.ordermanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/order-info")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<OrderInfo> getOrderInfoById(@PathVariable final String id,
      @RequestHeader(name = "requestId") final String requestId) {
    return userService.getOrderInfoById(requestId, id);
  }

}
