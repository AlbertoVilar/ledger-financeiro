package com.alber.ledgerfinanceiro.domain.exceptions;

public class AccountClosedException extends RuntimeException {

    public AccountClosedException(String message) {
        super(message);
    }
}
