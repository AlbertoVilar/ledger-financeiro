package com.alber.ledgerfinanceiro.adapter.in.web.mapper;

import com.alber.ledgerfinanceiro.adapter.in.web.dto.TransferRequest;
import com.alber.ledgerfinanceiro.adapter.in.web.dto.TransferResponse;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsCommand;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.Money;
import com.alber.ledgerfinanceiro.domain.model.Transfer;

import java.util.Currency;
import java.util.Objects;

public final class TransferWebMapper {

    private TransferWebMapper() {
    }

    public static TransferBetweenAccountsCommand toCommand(TransferRequest request) {
        Objects.requireNonNull(request, "A requisição é obrigatória.");

        return new TransferBetweenAccountsCommand(
                new AccountId(request.sourceAccountId()),
                new AccountId(request.destinationAccountId()),
                new Money(request.amount(), Currency.getInstance(request.currency()))
        );
    }

    public static TransferResponse toResponse(Transfer transfer) {
        Objects.requireNonNull(transfer, "A transferência é obrigatória.");

        return new TransferResponse(
                transfer.getId().id(),
                transfer.getSourceAccountId().id(),
                transfer.getDestinationAccountId().id(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency().getCurrencyCode(),
                transfer.getOccurredAt()
        );
    }
}
