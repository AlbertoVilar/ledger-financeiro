package com.alber.ledgerfinanceiro.domain.exceptions;

public class AccountHasBalanceException extends RuntimeException {

    public AccountHasBalanceException(String message) {
        super(message);
    }
}
