package com.alber.ledgerfinanceiro.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TransferId(
        UUID id
) {
    public TransferId {
        Objects.requireNonNull(id, "O identificador da transferência é obrigatório.");
    }
}
