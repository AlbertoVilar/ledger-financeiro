package com.alber.ledgerfinanceiro.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Locale;

public record CreateAccountRequest(
        @NotBlank(message = "A moeda é obrigatória.")
        @Pattern(regexp = "^[A-Z]{3}$", message = "A moeda deve usar o código ISO 4217 de três letras.")
        String currency
) {
    public CreateAccountRequest {
        if (currency != null) {
            currency = currency.trim().toUpperCase(Locale.ROOT);
        }
    }
}
