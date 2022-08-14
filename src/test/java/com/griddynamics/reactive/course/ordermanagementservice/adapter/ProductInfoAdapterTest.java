package com.griddynamics.reactive.course.ordermanagementservice.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.griddynamics.reactive.course.ordermanagementservice.domain.Product;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWireMock(port = 8082)
@ExtendWith(SpringExtension.class)
class ProductInfoAdapterTest {

  private static final String PRODUCT_ID   = "111";
  private static final String PRODUCT_CODE = "3852";
  private static final String PRODUCT_NAME = "IceCream";
  private static final Double SCORE        = 1689.37;
  private static final String JSON         =
      "{\"productId\": \"111\", \"productCode\": \"3852\", " + "\"productName\": \"IceCream\", \"score\": 1689.37}";

  @Autowired
  private ProductInfoAdapter productInfoAdapter;

  @Test
  void shouldGetProductNamesByProductCode() {
    WireMock.stubFor(WireMock
        .get(WireMock.urlMatching("/productInfoService/product/names\\?productCode=" + PRODUCT_CODE))
        .willReturn(WireMock.aResponse().withHeader("content-type", "application/json").withBody(JSON)));

    final Flux<Product> productFlux = productInfoAdapter.getProductNamesByProductCode(PRODUCT_CODE);

    StepVerifier.create(productFlux).assertNext(product -> {
      assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
      assertThat(product.getProductCode()).isEqualTo(PRODUCT_CODE);
      assertThat(product.getProductName()).isEqualTo(PRODUCT_NAME);
      assertThat(product.getScore()).isEqualTo(SCORE);
    }).verifyComplete();
  }

}
