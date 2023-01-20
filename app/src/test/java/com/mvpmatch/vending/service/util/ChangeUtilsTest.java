package com.mvpmatch.vending.service.util;

import com.mvpmatch.vending.entity.Deposit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChangeUtilsTest {

    private final Map<Integer, Deposit> depositsByCoin = Map.of(
            5, Deposit.builder().cents(5).build(),
            10, Deposit.builder().cents(10).build(),
            20, Deposit.builder().cents(20).build(),
            50, Deposit.builder().cents(50).build(),
            100, Deposit.builder().cents(100).build()
    );

    @Test
    void shouldReturnDepositChangeByOneCoin() {
        var deposits = List.of(depositsByCoin.get(10));
        var changeInCents = 10;

        List<List<Deposit>> depositChangeCombinations = new ArrayList<>();
        ChangeUtils.calculateDepositChange(deposits, changeInCents, new ArrayList<>(), depositChangeCombinations);

        assertThat(depositChangeCombinations.get(0).size()).isEqualTo(1);
        assertThat(depositChangeCombinations.get(0)
                                            .stream()
                                            .map(Deposit::getCents)
                                            .reduce(0, Integer::sum)).isEqualTo(changeInCents);
    }

    @Test
    void shouldReturnDepositChangeByTwoCoins() {
        var deposits = List.of(depositsByCoin.get(5), depositsByCoin.get(5), depositsByCoin.get(20));
        var changeInCents = 10;

        List<List<Deposit>> depositChangeCombinations = new ArrayList<>();
        ChangeUtils.calculateDepositChange(deposits, changeInCents, new ArrayList<>(), depositChangeCombinations);

        assertThat(depositChangeCombinations.get(0).size()).isEqualTo(2);
        assertThat(depositChangeCombinations.get(0)
                                            .stream()
                                            .map(Deposit::getCents)
                                            .reduce(0, Integer::sum)).isEqualTo(changeInCents);
    }

    @Test
    void shouldReturnDepositChangeByThreeCoins() {
        var deposits = List.of(depositsByCoin.get(5), depositsByCoin.get(5), depositsByCoin.get(20), depositsByCoin.get(50));
        var changeInCents = 75;

        List<List<Deposit>> depositChangeCombinations = new ArrayList<>();
        ChangeUtils.calculateDepositChange(deposits, changeInCents, new ArrayList<>(), depositChangeCombinations);

        assertThat(depositChangeCombinations.get(0).size()).isEqualTo(3);
        assertThat(depositChangeCombinations.get(0)
                                            .stream()
                                            .map(Deposit::getCents)
                                            .reduce(0, Integer::sum)).isEqualTo(changeInCents);
    }

    @Test
    void shouldNotReturnDepositChangeForThisAmount() {
        var deposits = List.of(depositsByCoin.get(5), depositsByCoin.get(20), depositsByCoin.get(50));
        var changeInCents = 30;

        List<List<Deposit>> depositChangeCombinations = new ArrayList<>();
        ChangeUtils.calculateDepositChange(deposits, changeInCents, new ArrayList<>(), depositChangeCombinations);

        assertThat(depositChangeCombinations).isEmpty();
    }
}