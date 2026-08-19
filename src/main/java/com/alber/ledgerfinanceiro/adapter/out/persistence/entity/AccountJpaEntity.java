package com.alber.ledgerfinanceiro.adapter.out.persistence.entity;

import com.alber.ledgerfinanceiro.domain.model.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
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

    protected AccountJpaEntity() {
    }

    public AccountJpaEntity(UUID id, AccountStatus status, BigDecimal balanceAmount, String balanceCurrency) {
        this.id = id;
        this.status = status;
        this.balanceAmount = balanceAmount;
        this.balanceCurrency = balanceCurrency;
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
}
