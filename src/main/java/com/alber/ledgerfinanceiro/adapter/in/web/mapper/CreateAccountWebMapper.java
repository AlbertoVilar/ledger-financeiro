package com.alber.ledgerfinanceiro.adapter.in.web.mapper;

import com.alber.ledgerfinanceiro.adapter.in.web.dto.CreateAccountRequest;
import com.alber.ledgerfinanceiro.adapter.in.web.dto.CreateAccountResponse;
import com.alber.ledgerfinanceiro.application.port.in.CreateAccountCommand;
import com.alber.ledgerfinanceiro.domain.model.Account;

import java.util.Currency;
import java.util.Objects;

public final class CreateAccountWebMapper {

    private CreateAccountWebMapper() {
    }

    public static CreateAccountCommand toCommand(CreateAccountRequest request) {
        Objects.requireNonNull(request, "A requisição é obrigatória.");

        return new CreateAccountCommand(Currency.getInstance(request.currency()));
    }

    public static CreateAccountResponse toResponse(Account account) {
        Objects.requireNonNull(account, "A conta é obrigatória.");

        return new CreateAccountResponse(
                account.getId().id(),
                account.getStatus(),
                account.getBalance().amount(),
                account.getBalance().currency().getCurrencyCode()
        );
    }
}
