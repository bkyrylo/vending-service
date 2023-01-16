package com.mvpmatch.vending.api.dto;

import com.mvpmatch.vending.api.validation.AllowedCoins;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepositRequest {

    @AllowedCoins
    private Integer depositInCents; // one coin
}
