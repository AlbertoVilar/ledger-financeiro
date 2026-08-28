package com.alber.ledgerfinanceiro.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferIdTest {

    @Test
    void shouldCreateTransferIdWithValidUuid() {
        var value = UUID.randomUUID();

        var transferId = new TransferId(value);

        assertEquals(value, transferId.id());
    }

    @Test
    void shouldNotCreateTransferIdWithNullValue() {
        assertThrows(NullPointerException.class, () -> new TransferId(null));
    }
}
