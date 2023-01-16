package com.mvpmatch.vending.api.dto;

import com.mvpmatch.vending.api.validation.AllowedProductCost;
import com.mvpmatch.vending.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;


@Builder
@Value
public class CreateUpdateProductRequest {

    @NotNull
    @Size(max = 128, message = "Product name should not exceeded 128 characters")
    String productName;

    @AllowedProductCost
    Integer cost;

    @NotNull
    @Min(value = 1, message = "Product amount should be greater than 0")
    Integer amountAvailable;

    public static Product asProduct(CreateUpdateProductRequest createProductRequest) {
        return Product.builder()
                      .productName(createProductRequest.getProductName())
                      .cost(createProductRequest.getCost())
                      .amountAvailable(createProductRequest.getAmountAvailable())
                      .build();
    }
}
