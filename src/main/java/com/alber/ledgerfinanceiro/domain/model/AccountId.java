package com.alber.ledgerfinanceiro.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AccountId(
        UUID id
) {
    public AccountId {
        Objects.requireNonNull(id);
    }
}
