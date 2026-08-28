package com.alber.ledgerfinanceiro.domain.model;

import com.alber.ledgerfinanceiro.domain.exceptions.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoneyTest {

    private static final Currency BRL = Currency.getInstance("BRL");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void shouldAddMoneyWhenCurrenciesAreTheSame() {
        // 1. Arrange (Criar duas instâncias de Money com BRL)
        var firstAmount = brl("100.00");
        var secondAmount = brl("50.00");

        // 2. Act (Executar o método de soma)
        var amount = firstAmount.add(secondAmount);

        // 3. Assert (Verificar se o valor resultante e a moeda estão corretos)
        // Assert com a ordem padrão (esperado, obtido)
        assertEquals(brl("150.00"), amount);
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        // 1. Arrange
        var brlAmount = brl("100.00");
        var usdAmount = usd("50.00");

        // 2. Act & Assert
        assertThrows(CurrencyMismatchException.class, () -> brlAmount.add(usdAmount));
    }

    @Test
    void shouldThrowExceptionWhenCalculatingWithNullMoney() {
        var amount = brl("100.00");

        assertThrows(NullPointerException.class, () -> amount.add(null));
        assertThrows(NullPointerException.class, () -> amount.subtract(null));
        assertThrows(NullPointerException.class, () -> amount.isGreaterThanOrEqual(null));
    }

    @Test
    void shouldSubtractMoneyWhenCurrenciesAreTheSame() {
        // 1. Arrange (Criar duas instâncias de Money com BRL)
        var initialAmount = brl("100.00");
        var amountToSubtract = brl("40.00");

        // 2. Act (Executar o método de subtração)
        var amount = initialAmount.subtract(amountToSubtract);

        // 3. Assert (Verificar se o valor resultante e a moeda estão corretos)
        assertEquals(brl("60.00"), amount);
    }

    @Test
    void shouldThrowExceptionWhenSubtractingDifferentCurrencies() {
        // 1. Arrange
        var brlAmount = brl("100.00");
        var usdAmount = usd("50.00");

        // 2. Act & Assert
        assertThrows(CurrencyMismatchException.class, () -> brlAmount.subtract(usdAmount));
    }

    @Test
    void shouldIdentifyPositiveAmounts() {
        assertTrue(brl("0.01").isPositive());
        assertFalse(brl("0.00").isPositive());
        assertFalse(brl("-0.01").isPositive());
    }

    @Test
    void shouldIdentifyZeroAmounts() {
        assertTrue(brl("0.00").isZero());
        assertFalse(brl("0.01").isZero());
        assertFalse(brl("-0.01").isZero());
    }

    @Test
    void shouldCompareAmountsWhenCurrenciesAreTheSame() {
        assertTrue(brl("100.00").isGreaterThanOrEqual(brl("100.00")));
        assertTrue(brl("100.00").isGreaterThanOrEqual(brl("50.00")));
        assertFalse(brl("50.00").isGreaterThanOrEqual(brl("100.00")));
    }

    @Test
    void shouldThrowExceptionWhenComparingDifferentCurrencies() {
        assertThrows(CurrencyMismatchException.class,
                () -> brl("100.00").isGreaterThanOrEqual(usd("50.00")));
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(null, BRL));
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.ONE, null));
    }

    @Test
    void shouldBeEqualWhenAmountsHaveDifferentScales() {
        var amountWithTwoDecimalPlaces = brl("100.00");
        var amountWithOneDecimalPlace = brl("100.0");

        assertEquals(amountWithTwoDecimalPlaces, amountWithOneDecimalPlace);
        assertEquals(
                amountWithTwoDecimalPlaces.hashCode(),
                amountWithOneDecimalPlace.hashCode()
        );
    }

    @Test
    void shouldNotBeEqualWhenCurrenciesAreDifferent() {
        var amountInBrl = brl("100.00");
        var amountInUsd = usd("100.00");

        assertFalse(amountInBrl.equals(amountInUsd));
    }

    private static Money brl(String amount) {
        return new Money(new BigDecimal(amount), BRL);
    }

    private static Money usd(String amount) {
        return new Money(new BigDecimal(amount), USD);
    }
}
