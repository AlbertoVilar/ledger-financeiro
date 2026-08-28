package com.alber.ledgerfinanceiro.domain.model;

import com.alber.ledgerfinanceiro.domain.exceptions.InvalidTransactionAmountException;
import com.alber.ledgerfinanceiro.domain.exceptions.SameAccountTransferException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    @Test
    void shouldCreateTransferSuccessfully() {
        // 1. Arrange
        var sourceAccountId = anAccountId();
        var destinationAccountId = anAccountId();
        var amount = brl("100.00");

        // 2. Act
        var transfer = Transfer.create(sourceAccountId, destinationAccountId, amount);

        // 3. Assert
        assertNotNull(transfer.getId());
        assertEquals(sourceAccountId, transfer.getSourceAccountId());
        assertEquals(destinationAccountId, transfer.getDestinationAccountId());
        assertEquals(amount, transfer.getAmount());
        assertNotNull(transfer.getOccurredAt());
    }

    @Test
    void shouldThrowExceptionWhenSourceAndDestinationAreTheSame() {
        // 1. Arrange (Usamos o mesmo AccountId para origem e destino)
        var accountId = anAccountId();
        var amount = brl("100.00");

        // 2. Act & Assert
        assertThrows(SameAccountTransferException.class, () -> {
            Transfer.create(accountId, accountId, amount);
        });

    }

    @Test
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        // 1. Arrange (Duas contas válidas e os valores inválidos: zero e negativo)
        var sourceAccountId = anAccountId();
        var destinationAccountId = anAccountId();
        var zeroAmount = brl("0.00");
        var negativeAmount = brl("-10.00");

        // 2. Act & Assert
        assertThrows(InvalidTransactionAmountException.class, () -> {
            Transfer.create(sourceAccountId,destinationAccountId, zeroAmount);
        });

        assertThrows(InvalidTransactionAmountException.class, () -> {
            Transfer.create(sourceAccountId,destinationAccountId, negativeAmount);
        });
    }

    @Test
    void shouldThrowExceptionWhenCreatingTransferWithNullRequiredData() {
        // 1. Arrange
        var sourceAccountId = anAccountId();
        var destinationAccountId = anAccountId();
        var amount = brl("100.00");

        // 2. Act & Assert
        assertThrows(NullPointerException.class,
                () -> Transfer.create(null, destinationAccountId, amount));
        assertThrows(NullPointerException.class,
                () -> Transfer.create(sourceAccountId, null, amount));
        assertThrows(NullPointerException.class,
                () -> Transfer.create(sourceAccountId, destinationAccountId, null));
    }

    private static AccountId anAccountId() {
        return new AccountId(UUID.randomUUID());
    }

    private static Money brl(String amount) {
        return new Money(new BigDecimal(amount), BRL);
    }
}
