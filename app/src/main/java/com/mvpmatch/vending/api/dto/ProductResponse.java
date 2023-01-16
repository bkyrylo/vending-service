package com.mvpmatch.vending.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvpmatch.vending.entity.Product;
import com.mvpmatch.vending.entity.Role;
import com.mvpmatch.vending.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    UUID id;

    String productName;

    Integer cost;

    Integer amountAvailable;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                              .id(product.getId())
                              .productName(product.getProductName())
                              .cost(product.getCost())
                              .amountAvailable(product.getAmountAvailable())
                              .build();
    }
}
