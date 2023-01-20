package com.mvpmatch.vending.service.util;

import com.mvpmatch.vending.entity.Deposit;
import java.util.ArrayList;
import java.util.List;

public class ChangeUtils {

    /*
     * Algorithm to find subsets of a set for a given sum.
     * deposits - a list of deposits, each one is a deposited coin of 5, 10, 20, 50, 100 cents;
     * changeInCents - the amount of change in cents we would like to obtain deposit subsets for;
     * partial - partial list, needed for the algorithm;
     * depositChangeCombinations - resulting list of possible deposits(coins) combinations. This is cents change coin combination.
     */
    public static void calculateDepositChange(List<Deposit> deposits, int changeInCents, List<Deposit> partial, List<List<Deposit>> depositChangeCombinations) {
        int sum = partial.stream().map(Deposit::getCents).reduce(0, Integer::sum);

        if (sum == changeInCents) {
            depositChangeCombinations.add(partial);
        } else if (sum < changeInCents) {
            for (int i = 0; i < deposits.size(); i++) {
                Deposit deposit = deposits.get(i);

                List<Deposit> remaining = deposits.subList(i + 1, deposits.size());
                List<Deposit> newPartial = new ArrayList<>(partial);
                newPartial.add(deposit);
                calculateDepositChange(remaining, changeInCents, newPartial, depositChangeCombinations);
            }
        }
    }
}
