package com.griddynamics.reactive.course.ordermanagementservice.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.griddynamics.reactive.course.ordermanagementservice.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static com.griddynamics.reactive.course.ordermanagementservice.utils.LogUtils.logOnError;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductInfoAdapter {

  public static final String GET_PRODUCT_NAMES_BY_PRODUCT_CODE_ENDPOINT = "/product/names";

  private final WebClient productInfoWebClient;

  public Flux<Product> getProductNamesByProductCode(String productCode) {
    var uri = UriComponentsBuilder
        .fromUriString(GET_PRODUCT_NAMES_BY_PRODUCT_CODE_ENDPOINT)
        .queryParam("productCode", productCode)
        .buildAndExpand()
        .toUriString();

    return productInfoWebClient
        .get()
        .uri(uri)
        .retrieve()
        .bodyToFlux(Product.class)
        .subscribeOn(Schedulers.boundedElastic())
        .switchIfEmpty(Flux.empty())
        .doOnEach(logOnError(error -> log.error("Error occurred while fetching data from ProductInfo service", error)))
        .onErrorResume(throwable -> Flux.empty());
  }

}
