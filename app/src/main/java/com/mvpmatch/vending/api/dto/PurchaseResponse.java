package com.mvpmatch.vending.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponse {

    Integer spentInCents;

    String productName;

    List<Integer> change;
}
