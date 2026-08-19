package com.alber.ledgerfinanceiro.application.port.in;

import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.Money;

import java.util.Objects;

public record TransferBetweenAccountsCommand(
        AccountId sourceAccountId,
        AccountId destinationAccountId,
        Money amount
) {
    public TransferBetweenAccountsCommand {
        Objects.requireNonNull(sourceAccountId, "A conta de origem é obrigatória.");
        Objects.requireNonNull(destinationAccountId, "A conta de destino é obrigatória.");
        Objects.requireNonNull(amount, "O valor da transferência é obrigatório.");

        if (!amount.isPositive()) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }
    }
}
