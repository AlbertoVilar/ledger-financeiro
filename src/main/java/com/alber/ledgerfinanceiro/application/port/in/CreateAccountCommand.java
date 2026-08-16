package com.alber.ledgerfinanceiro.application.port.in;

import java.util.Currency;
import java.util.Objects;

public record CreateAccountCommand(
        Currency currency
) {
    public CreateAccountCommand {
        Objects.requireNonNull(currency, "A moeda é obrigatória.");
    }
}