package com.alber.ledgerfinanceiro.domain.model;

import java.math.BigDecimal;
import java.util.Currency;

public class Account {

    private final AccountId id;
    private AccountStatus status;
    private Money balance;

    public Account(AccountId id, AccountStatus status, Money balance) {
        this.id = id;
        this.status = status;
        this.balance = balance;
    }
    public static Account create(AccountId id, Currency currency) {
        return new Account(
                id,
                AccountStatus.ACTIVE,
                new Money(BigDecimal.ZERO, currency)
        );
    }
    public AccountId getId() {
        return id;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Money getBalance() {
        return balance;
    }

    public void deposit(Money amount) {

        ensureActive();
        ensurePositive(amount);

        this.balance = balance.add(amount);
    }

    public void withdraw(Money amount) {
        ensureActive();
        ensurePositive(amount);

        if (!balance.isGreaterThanOrEqual(amount)) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        balance = balance.subtract(amount);
    }

    public void block() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("A conta encerrada não pode ser bloqueada.");
        }
        if (this.status == AccountStatus.BLOCKED) {
            throw new IllegalStateException("A conta já está bloqueada.");
        }

        this.status = AccountStatus.BLOCKED;
    }

    public void unblock() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("A conta encerrada não pode ser desbloqueada.");
        }
        if (this.status != AccountStatus.BLOCKED) {
            throw new IllegalStateException("A conta não está bloqueada.");
        }

        this.status = AccountStatus.ACTIVE;
    }

    public void close() {

        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("A conta já foi encerrada.");
        }
        if (!balance.isZero()) {
           throw new IllegalStateException("A conta só pode ser encerrada com saldo zerado.");
        }
        this.status = AccountStatus.CLOSED;
    }

    private void ensureActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("A conta não está ativa.");
        }
    }

    private void ensurePositive(Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("O valor da movimentação deve ser positivo.");
        }
    }

}
