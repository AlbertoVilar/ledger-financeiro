package com.alber.ledgerfinanceiro.domain.exceptions;

public class SameAccountTransferException extends RuntimeException {

    public SameAccountTransferException(String message) {
        super(message);
    }
}
