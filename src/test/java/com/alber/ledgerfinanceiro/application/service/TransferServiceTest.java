package com.alber.ledgerfinanceiro.application.service;

import com.alber.ledgerfinanceiro.application.exceptions.ResourceNotFoundException;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsCommand;
import com.alber.ledgerfinanceiro.application.port.out.LoadAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveTransferPort;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.InsufficientBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.SameAccountTransferException;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.AccountStatus;
import com.alber.ledgerfinanceiro.domain.model.Money;
import com.alber.ledgerfinanceiro.domain.model.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    @InjectMocks
    private TransferService transferService;

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private SaveAccountPort saveAccountPort;

    @Mock
    private SaveTransferPort saveTransferPort;

    @Test
    void shouldTransferSuccessfullyWhenAccountsAreValidAndBalanceIsSufficient() {
        var originAccount = anActiveAccount(new BigDecimal("100.00"));
        var destinationAccount = anActiveAccount(new BigDecimal("50.00"));
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                originAccount.getId(),
                destinationAccount.getId(),
                transferAmount
        );

        Mockito.when(loadAccountPort.findById(originAccount.getId()))
                .thenReturn(Optional.of(originAccount));
        Mockito.when(loadAccountPort.findById(destinationAccount.getId()))
                .thenReturn(Optional.of(destinationAccount));

        Mockito.when(saveTransferPort.save(Mockito.any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var transfer = transferService.execute(command);

        assertEquals(new Money(new BigDecimal("70.00"), BRL), originAccount.getBalance());
        assertEquals(new Money(new BigDecimal("80.00"), BRL), destinationAccount.getBalance());
        assertEquals(originAccount.getId(), transfer.getSourceAccountId());
        assertEquals(destinationAccount.getId(), transfer.getDestinationAccountId());
        assertEquals(transferAmount, transfer.getAmount());

        Mockito.verify(saveAccountPort).save(originAccount);
        Mockito.verify(saveAccountPort).save(destinationAccount);
        Mockito.verify(saveTransferPort).save(transfer);
        Mockito.verify(loadAccountPort).findById(originAccount.getId());
        Mockito.verify(loadAccountPort).findById(destinationAccount.getId());
        Mockito.verifyNoMoreInteractions(loadAccountPort);
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountNotFound() {
        var sourceAccountId = new AccountId(UUID.randomUUID());
        var destinationAccountId = new AccountId(UUID.randomUUID());
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                sourceAccountId,
                destinationAccountId,
                transferAmount
        );
        Mockito.when(loadAccountPort.findById(sourceAccountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferService.execute(command));

        Mockito.verifyNoInteractions(saveAccountPort, saveTransferPort);
        Mockito.verify(loadAccountPort).findById(sourceAccountId);
        Mockito.verifyNoMoreInteractions(loadAccountPort);
    }

    @Test
    void shouldThrowExceptionWhenDestinationAccountNotFound() {
        var sourceAccount = anActiveAccount(new BigDecimal("100.00"));
        var destinationAccountId = new AccountId(UUID.randomUUID());
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                sourceAccount.getId(),
                destinationAccountId,
                transferAmount
        );

        Mockito.when(loadAccountPort.findById(sourceAccount.getId()))
                .thenReturn(Optional.of(sourceAccount));
        Mockito.when(loadAccountPort.findById(destinationAccountId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferService.execute(command));

        Mockito.verifyNoInteractions(saveAccountPort, saveTransferPort);
        Mockito.verify(loadAccountPort).findById(sourceAccount.getId());
        Mockito.verify(loadAccountPort).findById(destinationAccountId);
        Mockito.verifyNoMoreInteractions(loadAccountPort);
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountHasInsufficientBalance() {
        var sourceAccount = anActiveAccount(new BigDecimal("10.00"));
        var destinationAccount = anActiveAccount(new BigDecimal("50.00"));
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                sourceAccount.getId(),
                destinationAccount.getId(),
                transferAmount
        );

        Mockito.when(loadAccountPort.findById(sourceAccount.getId()))
                .thenReturn(Optional.of(sourceAccount));
        Mockito.when(loadAccountPort.findById(destinationAccount.getId()))
                .thenReturn(Optional.of(destinationAccount));

        assertThrows(InsufficientBalanceException.class, () -> transferService.execute(command));

        Mockito.verify(loadAccountPort).findById(sourceAccount.getId());
        Mockito.verify(loadAccountPort).findById(destinationAccount.getId());
        Mockito.verifyNoInteractions(saveAccountPort, saveTransferPort);
        Mockito.verifyNoMoreInteractions(loadAccountPort);

    }

    @Test
    void shouldThrowExceptionWhenSourceAccountIsInactive() {
        var sourceAccount = anInactiveAccount(new BigDecimal("100.00"));
        var destinationAccount = anActiveAccount(new BigDecimal("50.00"));
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                sourceAccount.getId(),
                destinationAccount.getId(),
                transferAmount
        );

        Mockito.when(loadAccountPort.findById(sourceAccount.getId()))
                .thenReturn(Optional.of(sourceAccount));
        Mockito.when(loadAccountPort.findById(destinationAccount.getId()))
                .thenReturn(Optional.of(destinationAccount));

        assertThrows(AccountBlockedException.class, () -> transferService.execute(command));

        Mockito.verifyNoInteractions(saveAccountPort, saveTransferPort);
        Mockito.verify(loadAccountPort).findById(sourceAccount.getId());
        Mockito.verify(loadAccountPort).findById(destinationAccount.getId());
        Mockito.verifyNoMoreInteractions(loadAccountPort);
    }

    @Test
    void shouldThrowExceptionWhenDestinationAccountIsInactive() {
        var sourceAccount = anActiveAccount(new BigDecimal("100.00"));
        var destinationAccount = anInactiveAccount(new BigDecimal("50.00"));
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                sourceAccount.getId(),
                destinationAccount.getId(),
                transferAmount
        );

        Mockito.when(loadAccountPort.findById(sourceAccount.getId()))
                .thenReturn(Optional.of(sourceAccount));
        Mockito.when(loadAccountPort.findById(destinationAccount.getId()))
                .thenReturn(Optional.of(destinationAccount));

        assertThrows(AccountBlockedException.class, () -> transferService.execute(command));

        Mockito.verifyNoInteractions(saveAccountPort, saveTransferPort);
        Mockito.verify(loadAccountPort).findById(sourceAccount.getId());
        Mockito.verify(loadAccountPort).findById(destinationAccount.getId());
        Mockito.verifyNoMoreInteractions(loadAccountPort);
    }

    @Test
    void shouldThrowExceptionWhenTransferringToSameAccount() {
        var accountId = new AccountId(UUID.randomUUID());
        var transferAmount = new Money(new BigDecimal("30.00"), BRL);
        var command = new TransferBetweenAccountsCommand(
                accountId,
                accountId,
                transferAmount
        );

        assertThrows(SameAccountTransferException.class, () -> transferService.execute(command));

        Mockito.verifyNoInteractions(loadAccountPort, saveAccountPort, saveTransferPort);
    }

    private static Account anActiveAccount(BigDecimal initialBalance) {
        return new Account(
                new AccountId(UUID.randomUUID()),
                AccountStatus.ACTIVE,
                new Money(initialBalance, BRL),
                Instant.now()
        );
    }

    private static Account anInactiveAccount(BigDecimal initialBalance) {
        return new Account(
                new AccountId(UUID.randomUUID()),
                AccountStatus.BLOCKED,
                new Money(initialBalance, BRL),
                Instant.now()
        );
    }
}
