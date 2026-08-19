package com.alber.ledgerfinanceiro.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "A conta de origem é obrigatória.")
        UUID sourceAccountId,

        @NotNull(message = "A conta de destino é obrigatória.")
        UUID destinationAccountId,

        @NotNull(message = "O valor da transferência é obrigatório.")
        @DecimalMin(value = "0.00", inclusive = false, message = "O valor da transferência deve ser positivo.")
        BigDecimal amount,

        @NotBlank(message = "A moeda é obrigatória.")
        @Pattern(regexp = "^[A-Z]{3}$", message = "A moeda deve usar o código ISO 4217 de três letras.")
        String currency
) {
}
