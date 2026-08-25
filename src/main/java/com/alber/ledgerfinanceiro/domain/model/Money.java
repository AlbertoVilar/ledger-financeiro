package com.alber.ledgerfinanceiro.domain.model;

import com.alber.ledgerfinanceiro.domain.exceptions.CurrencyMismatchException;

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

    public boolean isZero() {
        return amount.signum() == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Money other)) {
            return false;
        }

        return currency.equals(other.currency())
                && amount.compareTo(other.amount()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "O valor a ser comparado/calculado é obrigatório.");

        if (!this.currency.equals(other.currency())) {
            throw new CurrencyMismatchException("As moedas devem ser iguais para realizar a operação.");
        }
    }

    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);

        return amount.compareTo(other.amount()) >= 0;
    }
}
