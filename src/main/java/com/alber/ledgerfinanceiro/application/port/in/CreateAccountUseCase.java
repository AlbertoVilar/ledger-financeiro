package com.alber.ledgerfinanceiro.application.port.in;

import com.alber.ledgerfinanceiro.domain.model.Account;

public interface CreateAccountUseCase {

    Account create(CreateAccountCommand command);
}
