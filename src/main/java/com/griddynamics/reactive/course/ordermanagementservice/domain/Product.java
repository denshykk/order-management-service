package com.griddynamics.reactive.course.ordermanagementservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  private String productId;
  private String productCode;
  private String productName;
  private double score;

}
