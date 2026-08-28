package com.alber.ledgerfinanceiro.domain.model;

import com.alber.ledgerfinanceiro.domain.exceptions.AccountBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountClosedException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountHasBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountNotBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.InsufficientBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.InvalidAccountBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.InvalidTransactionAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    @Test
    void shouldCreateActiveAccountWithZeroBalance() {
        // Cria uma conta ativa com saldo zero e data de abertura.
        var accountId = new AccountId(UUID.randomUUID());
        var account = Account.create(accountId, BRL);

        assertEquals(accountId, account.getId());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(new Money(BigDecimal.ZERO, BRL), account.getBalance());
        assertNotNull(account.getOpenedAt());
    }

    @Test
    void shouldDepositSuccessfullyWhenAccountIsActive() {
        // Adiciona um valor positivo ao saldo de uma conta ativa.
        var account = anActiveAccount(BigDecimal.ZERO);
        var depositAmount = new Money(new BigDecimal("100.00"), BRL);
        account.deposit(depositAmount);

        assertEquals(new Money(new BigDecimal("100.00"), BRL), account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfullyWhenThereIsEnoughBalance() {
        // Remove um valor quando a conta possui saldo suficiente.
        var account = anActiveAccount(new BigDecimal("100.00"));
        account.withdraw(new Money(new BigDecimal("25.00"), BRL));

        assertEquals(new Money(new BigDecimal("75.00"), BRL), account.getBalance());
    }

    @Test
    void shouldWithdrawEntireAvailableBalance() {
        // Permite saque igual ao saldo e deixa a conta com saldo zero.
        var account = anActiveAccount(new BigDecimal("100.00"));

        account.withdraw(new Money(new BigDecimal("100.00"), BRL));

        assertEquals(new Money(BigDecimal.ZERO, BRL), account.getBalance());
    }

    @Test
    void shouldNotWithdrawWhenBalanceIsInsufficient() {
        // Impede saque maior que o saldo e preserva o valor atual.
        var account = anActiveAccount(new BigDecimal("100.00"));

        assertThrows(InsufficientBalanceException.class,
                () -> account.withdraw(new Money(new BigDecimal("150.00"), BRL)));

        assertEquals(new Money(new BigDecimal("100.00"), BRL), account.getBalance());
    }

    @Test
    void shouldNotDepositZeroOrNegativeAmount() {
        // Impede depósitos com valor zero ou negativo.
        var account = anActiveAccount(new BigDecimal("100.00"));

        assertThrows(InvalidTransactionAmountException.class,
                () -> account.deposit(new Money(BigDecimal.ZERO, BRL)));
        assertThrows(InvalidTransactionAmountException.class,
                () -> account.deposit(new Money(new BigDecimal("-10.00"), BRL)));

        assertEquals(new Money(new BigDecimal("100.00"), BRL), account.getBalance());
    }

    @Test
    void shouldNotWithdrawZeroOrNegativeAmount() {
        // Impede saques com valor zero ou negativo.
        var account = anActiveAccount(new BigDecimal("100.00"));

        assertThrows(InvalidTransactionAmountException.class,
                () -> account.withdraw(new Money(BigDecimal.ZERO, BRL)));
        assertThrows(InvalidTransactionAmountException.class,
                () -> account.withdraw(new Money(new BigDecimal("-10.00"), BRL)));

        assertEquals(new Money(new BigDecimal("100.00"), BRL), account.getBalance());
    }

    @Test
    void shouldNotWithdrawWhenAccountIsBlocked() {
        // Impede saques de uma conta bloqueada.
        var account = anActiveAccount(new BigDecimal("100.00"));
        account.block();

        assertThrows(AccountBlockedException.class,
                () -> account.withdraw(new Money(new BigDecimal("50.00"), BRL)));

        assertEquals(new Money(new BigDecimal("100.00"), BRL), account.getBalance());
    }

    @Test
    void shouldNotDepositWhenAccountIsBlocked() {
        // Impede depósitos em uma conta bloqueada.
        var account = anActiveAccount(new BigDecimal("100.00"));
        account.block();

        assertThrows(AccountBlockedException.class,
                () -> account.deposit(new Money(new BigDecimal("50.00"), BRL)));

        assertEquals(new Money(new BigDecimal("100.00"), BRL), account.getBalance());
    }

    @Test
    void shouldNotCloseAccountWhenBalanceIsGreaterThanZero() {
        // Impede o encerramento de uma conta que ainda possui saldo.
        var account = anActiveAccount(new BigDecimal("100.00"));

        assertThrows(AccountHasBalanceException.class, account::close);

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void shouldBlockAnActiveAccount() {
        // Altera o status de uma conta ativa para bloqueada.
        var account = anActiveAccount(BigDecimal.ZERO);

        account.block();

        assertEquals(AccountStatus.BLOCKED, account.getStatus());
    }

    @Test
    void shouldNotBlockAnAlreadyBlockedAccount() {
        // Impede o bloqueio duplicado de uma conta já bloqueada.
        var account = anActiveAccount(BigDecimal.ZERO);
        account.block();

        assertThrows(AccountBlockedException.class, account::block);
        assertEquals(AccountStatus.BLOCKED, account.getStatus());
    }

    @Test
    void shouldNotBlockAnAccountThatIsClosed() {
        // Impede o bloqueio de uma conta encerrada.
        var account = aClosedAccount();

        assertThrows(AccountClosedException.class, account::block);
        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    void shouldUnblockABlockedAccount() {
        // Restaura uma conta bloqueada para o status ativo.
        var account = anActiveAccount(BigDecimal.ZERO);
        account.block();

        account.unblock();

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void shouldNotUnblockAnAccountThatIsNotBlocked() {
        // Impede o desbloqueio de uma conta que já está ativa.
        var account = anActiveAccount(BigDecimal.ZERO);

        assertThrows(AccountNotBlockedException.class, account::unblock);
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void shouldCloseAccountWithZeroBalance() {
        // Encerra uma conta quando seu saldo está zerado.
        var account = anActiveAccount(BigDecimal.ZERO);

        account.close();

        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    void shouldNotCloseAnAlreadyClosedAccount() {
        // Impede o encerramento duplicado de uma conta já encerrada.
        var account = aClosedAccount();

        assertThrows(AccountClosedException.class, account::close);
        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    void shouldNotDepositWhenAccountIsClosed() {
        // Impede depósitos em uma conta encerrada.
        var account = aClosedAccount();

        assertThrows(AccountClosedException.class,
                () -> account.deposit(new Money(new BigDecimal("10.00"), BRL)));
    }

    @Test
    void shouldNotWithdrawWhenAccountIsClosed() {
        // Impede saques em uma conta encerrada.
        var account = aClosedAccount();

        assertThrows(AccountClosedException.class,
                () -> account.withdraw(new Money(new BigDecimal("10.00"), BRL)));
    }

    @Test
    void shouldNotUnblockWhenAccountIsClosed() {
        // Impede o desbloqueio de uma conta encerrada.
        var account = aClosedAccount();

        assertThrows(AccountClosedException.class, account::unblock);
    }

    @Test
    void shouldPreserveOpenedAtWhenReconstitutingAccount() {
        // Preserva a data original de abertura recebida na reconstituição.
        var openedAt = Instant.parse("2026-08-22T12:00:00Z");
        var account = new Account(
                new AccountId(UUID.randomUUID()),
                AccountStatus.ACTIVE,
                new Money(BigDecimal.ZERO, BRL),
                openedAt
        );

        assertEquals(openedAt, account.getOpenedAt());
    }

    @Test
    void shouldNotCreateAccountWithNegativeInitialBalance() {
        // Impede a reconstituição de uma conta com saldo inicial negativo.
        assertThrows(InvalidAccountBalanceException.class, () -> new Account(
                new AccountId(UUID.randomUUID()),
                AccountStatus.ACTIVE,
                new Money(new BigDecimal("-0.01"), BRL),
                Instant.now()
        ));
    }

    private Account anActiveAccount(BigDecimal initialBalance) {
        return new Account(
                new AccountId(UUID.randomUUID()),
                AccountStatus.ACTIVE,
                new Money(initialBalance, BRL),
                Instant.now()
        );
    }

    private Account aClosedAccount() {
        var account = anActiveAccount(BigDecimal.ZERO);
        account.close();
        return account;
    }
}
