package com.alber.ledgerfinanceiro.domain.exceptions;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(String message) {
        super(message);
    }
}
