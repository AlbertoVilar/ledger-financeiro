package com.alber.ledgerfinanceiro.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Money(
        BigDecimal amount,
        Currency currency
) {

    public Money {
        Objects.requireNonNull(amount, "O valor é obrigatório.");
        Objects.requireNonNull(currency, "A moeda é obrigatória.");
    }

    public Money add(Money other) {
        validateSameCurrency(other);

        return new Money(amount.add(other.amount()), currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);

        return new Money(amount.subtract(other.amount()), currency);
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "O valor a ser comparado/calculado é obrigatório.");

        if (!this.currency.equals(other.currency())) {
            throw new IllegalArgumentException("As moedas devem ser iguais para realizar a operação.");
        }
    }

    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);

        return amount.compareTo(other.amount()) >= 0;
    }
}
