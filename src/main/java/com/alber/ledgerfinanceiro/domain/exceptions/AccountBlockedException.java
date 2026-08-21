package com.alber.ledgerfinanceiro.domain.exceptions;

public class AccountBlockedException extends RuntimeException {

    public AccountBlockedException(String message) {
        super(message);
    }
}
