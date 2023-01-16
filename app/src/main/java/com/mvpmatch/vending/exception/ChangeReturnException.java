package com.mvpmatch.vending.exception;

public class ChangeReturnException extends RuntimeException {

    public ChangeReturnException() {
        super("Cannot return the change");
    }
}
