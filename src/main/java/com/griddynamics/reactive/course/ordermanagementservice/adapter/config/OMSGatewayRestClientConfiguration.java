package com.griddynamics.reactive.course.ordermanagementservice.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OMSGatewayRestClientConfiguration {

  public static final String ORDER_SEARCH_BASEURL = "http://localhost:8081/orderSearchService";
  public static final String PRODUCT_INFO_BASEURL = "http://localhost:8082/productInfoService";

  @Bean
  public WebClient orderSearchWebClient() {
    return WebClient.builder().baseUrl(ORDER_SEARCH_BASEURL).build();
  }

  @Bean
  public WebClient productInfoWebClient() {
    return WebClient.builder().baseUrl(PRODUCT_INFO_BASEURL).build();
  }

}
