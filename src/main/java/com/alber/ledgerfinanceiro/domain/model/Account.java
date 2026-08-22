package com.alber.ledgerfinanceiro.domain.model;

import com.alber.ledgerfinanceiro.domain.exceptions.AccountBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountClosedException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountHasBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountNotBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.InsufficientBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.InvalidTransactionAmountException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

public class Account {

    private final AccountId id;
    private final Instant openedAt;
    private AccountStatus status;
    private Money balance;

    public Account(AccountId id, AccountStatus status, Money balance, Instant openedAt) {
        this.id = id;
        this.status = status;
        this.balance = balance;
        this.openedAt = openedAt;
    }
    public static Account create(AccountId id, Currency currency) {
        return new Account(
                id,
                AccountStatus.ACTIVE,
                new Money(BigDecimal.ZERO, currency),
                Instant.now()
        );
    }
    public AccountId getId() {
        return id;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getOpenedAt() {
        return openedAt;
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
            throw new InsufficientBalanceException("Saldo insuficiente");
        }
        balance = balance.subtract(amount);
    }

    public void block() {
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountClosedException("A conta encerrada não pode ser bloqueada.");
        }
        if (this.status == AccountStatus.BLOCKED) {
            throw new AccountBlockedException("A conta já está bloqueada.");
        }

        this.status = AccountStatus.BLOCKED;
    }

    public void unblock() {
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountClosedException("A conta encerrada não pode ser desbloqueada.");
        }
        if (this.status != AccountStatus.BLOCKED) {
            throw new AccountNotBlockedException("A conta não está bloqueada.");
        }

        this.status = AccountStatus.ACTIVE;
    }

    public void close() {

        if (this.status == AccountStatus.CLOSED) {
            throw new AccountClosedException("A conta já foi encerrada.");
        }
        if (!balance.isZero()) {
           throw new AccountHasBalanceException("A conta só pode ser encerrada com saldo zerado.");
        }
        this.status = AccountStatus.CLOSED;
    }

    private void ensureActive() {
        if (this.status == AccountStatus.BLOCKED) {
            throw new AccountBlockedException("A conta está bloqueada.");
        }
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountClosedException("A conta está encerrada.");
        }
    }

    private void ensurePositive(Money amount) {
        if (!amount.isPositive()) {
            throw new InvalidTransactionAmountException("O valor da movimentação deve ser positivo.");
        }
    }

}
