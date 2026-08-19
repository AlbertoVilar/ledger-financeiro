package com.alber.ledgerfinanceiro.application.service;

import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsCommand;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsUseCase;
import com.alber.ledgerfinanceiro.application.port.out.LoadAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveTransferPort;
import com.alber.ledgerfinanceiro.domain.model.Transfer;
import org.springframework.transaction.annotation.Transactional;

public class TransferService implements TransferBetweenAccountsUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransferPort saveTransferPort;

    public TransferService(
            LoadAccountPort loadAccountPort,
            SaveAccountPort saveAccountPort,
            SaveTransferPort saveTransferPort
    ) {
        this.loadAccountPort = loadAccountPort;
        this.saveAccountPort = saveAccountPort;
        this.saveTransferPort = saveTransferPort;
    }

    @Override
    @Transactional
    public Transfer execute(TransferBetweenAccountsCommand command) {
        var sourceAccount = loadAccountPort.findById(command.sourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta de origem não encontrada: " + command.sourceAccountId().id()
                ));

        var destinationAccount = loadAccountPort.findById(command.destinationAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta de destino não encontrada: " + command.destinationAccountId().id()
                ));

        sourceAccount.withdraw(command.amount());
        destinationAccount.deposit(command.amount());

        saveAccountPort.save(sourceAccount);
        saveAccountPort.save(destinationAccount);

        var newTransfer = Transfer.create(
                sourceAccount.getId(),
                destinationAccount.getId(),
                command.amount()
        );

        return saveTransferPort.save(newTransfer);
    }
}
