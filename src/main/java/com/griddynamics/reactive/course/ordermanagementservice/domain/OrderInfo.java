package com.griddynamics.reactive.course.ordermanagementservice.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.annotation.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderInfo {

  @NonNull
  private String orderNumber;
  @NonNull
  private String userName;
  @NonNull
  private String phoneNumber;
  @NonNull
  private String productCode;
  private String productName;
  private String productId;

}
