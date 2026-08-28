package com.alber.ledgerfinanceiro.domain.exceptions;

public class InvalidAccountBalanceException extends RuntimeException {

    public InvalidAccountBalanceException(String message) {
        super(message);
    }
}
