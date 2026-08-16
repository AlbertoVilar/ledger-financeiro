package com.alber.ledgerfinanceiro.application.port.out;

import com.alber.ledgerfinanceiro.domain.model.Account;

public interface SaveAccountPort {

    Account save(Account account);
}
