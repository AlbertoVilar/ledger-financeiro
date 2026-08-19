package com.alber.ledgerfinanceiro.application.port.out;

import com.alber.ledgerfinanceiro.domain.model.Transfer;

public interface SaveTransferPort {

    Transfer save(Transfer transfer);
}
