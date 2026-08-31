package com.alber.ledgerfinanceiro.application.service;

import com.alber.ledgerfinanceiro.application.port.in.CreateAccountCommand;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CreateAccountServiceTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    @InjectMocks
    private CreateAccountService createAccountService;

    @Mock
    private SaveAccountPort saveAccountPort;

    private static CreateAccountCommand aCreateAccountCommand() {
        return new CreateAccountCommand(BRL);
    }

    @Test
    void shouldCreateActiveAccountWithZeroBalanceAndRequestedCurrency() {
        // 1. Arrange
        var command = aCreateAccountCommand();

        Mockito.when(saveAccountPort.save(Mockito.any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        var account = createAccountService.create(command);

        // 3. Assert
        assertNotNull(account.getId());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertTrue(account.getBalance().isZero());
        assertEquals(BRL, account.getBalance().currency());
        assertNotNull(account.getOpenedAt());
        Mockito.verify(saveAccountPort).save(account);
        Mockito.verifyNoMoreInteractions(saveAccountPort);
    }
}
