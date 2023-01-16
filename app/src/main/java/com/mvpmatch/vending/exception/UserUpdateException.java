package com.mvpmatch.vending.exception;

public class UserUpdateException extends RuntimeException {

    public UserUpdateException() {
        super("User update exception");
    }

    public UserUpdateException(String message) {
        super(message);
    }
}
