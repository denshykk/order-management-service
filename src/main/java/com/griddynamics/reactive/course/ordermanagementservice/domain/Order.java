package com.griddynamics.reactive.course.ordermanagementservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  private String phoneNumber;
  private String orderNumber;
  private String productCode;

}
