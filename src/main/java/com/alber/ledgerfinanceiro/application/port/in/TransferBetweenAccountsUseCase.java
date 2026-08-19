package com.alber.ledgerfinanceiro.application.port.in;

import com.alber.ledgerfinanceiro.domain.model.Transfer;

public interface TransferBetweenAccountsUseCase {

    Transfer execute(TransferBetweenAccountsCommand command);
}
