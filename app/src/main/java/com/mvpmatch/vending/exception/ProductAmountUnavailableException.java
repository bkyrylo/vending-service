package com.mvpmatch.vending.exception;

public class ProductAmountUnavailableException extends RuntimeException {

    public ProductAmountUnavailableException() {
        super("Product amount unavailable");
    }
}
