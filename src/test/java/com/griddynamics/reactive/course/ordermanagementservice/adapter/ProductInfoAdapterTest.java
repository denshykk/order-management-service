package com.griddynamics.reactive.course.ordermanagementservice.adapter;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.griddynamics.reactive.course.ordermanagementservice.domain.Product;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ProductInfoAdapterTest {

  private static final String PRODUCT_ID     = "111";
  private static final String PRODUCT_CODE   = "3852";
  private static final String PRODUCT_NAME   = "IceCream";
  private static final Double SCORE          = 1689.37;
  private static final String JSON_FILE_PATH = "/Users/dtykhonov/IdeaProjects/Reactive_Paradigm_FOR_STUDENTS/order" +
      "-management-service/src/test/resources/product-info-adapter-response.json";

  private ProductInfoAdapter productInfoAdapter;
  private String             json;

  @BeforeEach
  @SneakyThrows
  void setUp() {
    json = Files.readAllLines(Path.of(JSON_FILE_PATH)).get(0);

    WebClient webClient = WebClient
        .builder()
        .exchangeFunction(request -> Mono.just(ClientResponse
            .create(HttpStatus.OK)
            .header("content-type", "application/json")
            .body(json)
            .build()))
        .build();

    productInfoAdapter = new ProductInfoAdapter(webClient);
  }

  @Test
  void shouldGetProductNamesByProductCode() {
    final Flux<Product> productFlux = productInfoAdapter.getProductNamesByProductCode(PRODUCT_CODE);

    StepVerifier.create(productFlux).assertNext(product -> {
      assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
      assertThat(product.getProductCode()).isEqualTo(PRODUCT_CODE);
      assertThat(product.getProductName()).isEqualTo(PRODUCT_NAME);
      assertThat(product.getScore()).isEqualTo(SCORE);
    }).verifyComplete();
  }

}
