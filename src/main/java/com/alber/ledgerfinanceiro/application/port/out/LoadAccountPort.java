package com.alber.ledgerfinanceiro.application.port.out;

import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;

import java.util.Optional;

public interface LoadAccountPort {

    Optional<Account> findById(AccountId id);
}
