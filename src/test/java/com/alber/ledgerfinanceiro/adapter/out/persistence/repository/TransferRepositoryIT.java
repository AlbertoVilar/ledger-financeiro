package com.alber.ledgerfinanceiro.adapter.out.persistence.repository;

import com.alber.ledgerfinanceiro.adapter.out.persistence.TransferPersistenceAdapter;
import com.alber.ledgerfinanceiro.adapter.out.persistence.entity.TransferJpaEntity;
import com.alber.ledgerfinanceiro.adapter.out.persistence.mapper.TransferPersistenceMapper;
import com.alber.ledgerfinanceiro.config.JpaAuditingConfig;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.Money;
import com.alber.ledgerfinanceiro.domain.model.Transfer;
import com.alber.ledgerfinanceiro.domain.model.TransferId;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({TransferPersistenceAdapter.class, JpaAuditingConfig.class})
class TransferRepositoryIT {

    private static final Currency BRL = Currency.getInstance("BRL");

    @Autowired
    private TransferPersistenceAdapter transferAdapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveTransfer() {
        // 1. Arrange (Preparação)
        var transfer = aTransfer();

        // 2. Act (Ação)
        var savedTransfer = transferAdapter.save(transfer);

        entityManager.flush();
        entityManager.clear();

        var persistedTransfer = entityManager.find(TransferJpaEntity.class, savedTransfer.getId().id());

        // 3. Assert (Verificação)
        assertNotNull(persistedTransfer);
        assertNotNull(persistedTransfer.getCreatedAt());

        var loadedTransfer = TransferPersistenceMapper.toDomain(persistedTransfer);
        assertEquals(savedTransfer.getId(), loadedTransfer.getId());
        assertEquals(savedTransfer.getSourceAccountId(), loadedTransfer.getSourceAccountId());
        assertEquals(savedTransfer.getDestinationAccountId(), loadedTransfer.getDestinationAccountId());
        assertEquals(savedTransfer.getAmount(), loadedTransfer.getAmount());
        assertEquals(savedTransfer.getOccurredAt(), loadedTransfer.getOccurredAt());
    }

    private Transfer aTransfer() {
        return new Transfer(
                new TransferId(UUID.randomUUID()),
                new AccountId(UUID.randomUUID()),
                new AccountId(UUID.randomUUID()),
                new Money(new BigDecimal("100.00"), BRL),
                Instant.now().truncatedTo(ChronoUnit.MICROS)
        );
    }
}
