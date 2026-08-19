package com.alber.ledgerfinanceiro.adapter.in.web.dto;

import com.alber.ledgerfinanceiro.domain.model.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountResponse(
        UUID id,
        AccountStatus status,
        BigDecimal balance,
        String currency
) {
}
