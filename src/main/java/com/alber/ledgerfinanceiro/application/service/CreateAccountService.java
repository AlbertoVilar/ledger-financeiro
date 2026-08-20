package com.alber.ledgerfinanceiro.application.service;

import com.alber.ledgerfinanceiro.application.port.in.CreateAccountCommand;
import com.alber.ledgerfinanceiro.application.port.in.CreateAccountUseCase;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;

import java.util.UUID;

public class CreateAccountService implements CreateAccountUseCase {

    private final SaveAccountPort accountPort;

    public CreateAccountService(SaveAccountPort accountPort) {
        this.accountPort = accountPort;
    }

    @Override
    public Account create(CreateAccountCommand command) {

        var newAccount = Account.create(
                new AccountId(UUID.randomUUID()),
                command.currency()

        );
        return accountPort.save(newAccount);
    }
}
