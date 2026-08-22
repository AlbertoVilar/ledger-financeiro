package com.alber.ledgerfinanceiro.domain.exceptions;

public class AccountNotBlockedException extends RuntimeException {

    public AccountNotBlockedException(String message) {
        super(message);
    }
}
