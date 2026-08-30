package com.alber.ledgerfinanceiro.adapter.out.persistence.repository;

import com.alber.ledgerfinanceiro.adapter.out.persistence.AccountPersistenceAdapter;
import com.alber.ledgerfinanceiro.config.JpaAuditingConfig;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.AccountStatus;
import com.alber.ledgerfinanceiro.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({AccountPersistenceAdapter.class, JpaAuditingConfig.class})
public class AccountRepositoryIT {

    private static final Currency BRL = Currency.getInstance("BRL");

    @Autowired
    private AccountPersistenceAdapter accountAdapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndLoadAccountById() {
        // 1. Arrange (Preparação)
        var account = anActiveAccount(BigDecimal.ZERO);
        // 2. Act (Ação)
        var accountSaved = accountAdapter.save(account);

        entityManager.flush();
        entityManager.clear();

        var result = accountAdapter.findById(accountSaved.getId());
        // 3. Assert (Verificação)
        assertTrue(result.isPresent());

        var accountLoaded = result.orElseThrow();
        assertEquals(accountSaved.getId(), accountLoaded.getId());
        assertEquals(accountSaved.getStatus(), accountLoaded.getStatus());
        assertEquals(accountSaved.getBalance(), accountLoaded.getBalance());
        assertEquals(accountSaved.getOpenedAt(), accountLoaded.getOpenedAt());
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        // 1. Arrange (Preparação)
        var accountId = new AccountId(UUID.randomUUID());
        // 2. Act (Ação)
        var result = accountAdapter.findById(accountId);
        // 3. Assert (Verificação)
        assertTrue(result.isEmpty());
    }

    private Account anActiveAccount(BigDecimal initialBalance) {
        return new Account(
                new AccountId(UUID.randomUUID()),
                AccountStatus.ACTIVE,
                new Money(initialBalance, BRL),
                Instant.now().truncatedTo(ChronoUnit.MICROS)
        );
    }
}
