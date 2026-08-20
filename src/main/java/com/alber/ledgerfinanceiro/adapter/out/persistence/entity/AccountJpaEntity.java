package com.alber.ledgerfinanceiro.adapter.out.persistence.entity;

import com.alber.ledgerfinanceiro.domain.model.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class AccountJpaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "balance_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAmount;

    @Column(name = "balance_currency", nullable = false, length = 3)
    private String balanceCurrency;

    @Column(name = "opened_at", nullable = false, updatable = false)
    private Instant openedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AccountJpaEntity() {
    }

    public AccountJpaEntity(
            UUID id,
            AccountStatus status,
            BigDecimal balanceAmount,
            String balanceCurrency,
            Instant openedAt
    ) {
        this.id = id;
        this.status = status;
        this.balanceAmount = balanceAmount;
        this.balanceCurrency = balanceCurrency;
        this.openedAt = openedAt;
    }

    public UUID getId() {
        return id;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public String getBalanceCurrency() {
        return balanceCurrency;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
