package com.mvpmatch.vending.exception;

public class UserNotEnoughFundsException extends RuntimeException {

    public UserNotEnoughFundsException() {
        super("User doesn't have enough funds");
    }

    public UserNotEnoughFundsException(String message) {
        super(message);
    }
}
