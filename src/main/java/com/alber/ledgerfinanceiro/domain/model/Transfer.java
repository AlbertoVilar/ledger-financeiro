package com.alber.ledgerfinanceiro.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transfer {

    private final TransferId id;
    private final AccountId sourceAccountId;
    private final AccountId destinationAccountId;
    private final Money amount;
    private final Instant occurredAt;

    public static Transfer create(AccountId sourceAccountId, AccountId destinationAccountId, Money amount) {
        return new Transfer(
                new TransferId(UUID.randomUUID()),
                sourceAccountId,
                destinationAccountId,
                amount,
                Instant.now()
        );
    }

    public Transfer(TransferId id, AccountId sourceAccountId, AccountId destinationAccountId,
                    Money amount, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "O identificador da transferência é obrigatório.");
        this.sourceAccountId = Objects.requireNonNull(sourceAccountId, "A conta de origem é obrigatória.");
        this.destinationAccountId = Objects.requireNonNull(destinationAccountId, "A conta de destino é obrigatória.");
        this.amount = Objects.requireNonNull(amount, "O valor da transferência é obrigatório.");
        this.occurredAt = Objects.requireNonNull(occurredAt, "A data da transferência é obrigatória.");

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("A conta de origem deve ser diferente da conta de destino.");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }
    }

    public TransferId getId() {
        return id;
    }

    public AccountId getSourceAccountId() {
        return sourceAccountId;
    }

    public AccountId getDestinationAccountId() {
        return destinationAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
