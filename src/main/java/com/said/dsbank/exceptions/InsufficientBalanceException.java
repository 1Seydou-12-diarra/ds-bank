package com.said.dsbank.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String error)
    {
        super(error);
    }
}
