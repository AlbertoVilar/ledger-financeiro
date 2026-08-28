package com.alber.ledgerfinanceiro.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountIdTest {

    @Test
    void shouldCreateAccountIdWithValidUuid() {
        var value = UUID.randomUUID();

        var accountId = new AccountId(value);

        assertEquals(value, accountId.id());
    }

    @Test
    void shouldNotCreateAccountIdWithNullValue() {
        assertThrows(NullPointerException.class, () -> new AccountId(null));
    }
}
