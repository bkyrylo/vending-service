package com.mvpmatch.vending.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseRequest {

    @NotNull
    @Min(1)
    Integer amount;
}
