package com.alber.ledgerfinanceiro.adapter.in.web.controller;

import com.alber.ledgerfinanceiro.adapter.in.web.dto.TransferRequest;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsCommand;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsUseCase;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.Money;
import com.alber.ledgerfinanceiro.domain.model.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferBetweenAccountsUseCase useCase;

    @Test
    void shouldCreateTransferSuccessfully() throws Exception {
        // 1. Arrange
        var sourceAccountId = UUID.randomUUID();
        var destinationAccountId = UUID.randomUUID();
        var amount = new BigDecimal("50.00");
        var request = new TransferRequest(sourceAccountId, destinationAccountId, amount, "BRL");
        var requestBody = objectMapper.writeValueAsString(request);
        var command = new TransferBetweenAccountsCommand(
                new AccountId(sourceAccountId),
                new AccountId(destinationAccountId),
                new Money(amount, BRL)
        );
        var transfer = Transfer.create(
                new AccountId(sourceAccountId),
                new AccountId(destinationAccountId),
                new Money(amount, BRL)
        );

        when(useCase.execute(command)).thenReturn(transfer);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transfer.getId().id().toString()))
                .andExpect(jsonPath("$.sourceAccountId").value(sourceAccountId.toString()))
                .andExpect(jsonPath("$.destinationAccountId").value(destinationAccountId.toString()))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.occurredAt").exists());

        verify(useCase).execute(command);
    }

    @Test
    void shouldReturn422WhenSourceAccountIdIsMissing() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "destinationAccountId": "%s",
                  "amount": 50.00,
                  "currency": "BRL"
                }
                """.formatted(UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "sourceAccountId", "A conta de origem é obrigatória.");
    }

    @Test
    void shouldReturn422WhenDestinationAccountIdIsMissing() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "amount": 50.00,
                  "currency": "BRL"
                }
                """.formatted(UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "destinationAccountId", "A conta de destino é obrigatória.");
    }

    @Test
    void shouldReturn422WhenAmountIsMissing() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "currency": "BRL"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "amount", "O valor da transferência é obrigatório.");
    }

    @Test
    void shouldReturn422WhenAmountIsZero() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": 0,
                  "currency": "BRL"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "amount", "O valor da transferência deve ser positivo.");
    }

    @Test
    void shouldReturn422WhenAmountIsNegative() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": -10.00,
                  "currency": "BRL"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "amount", "O valor da transferência deve ser positivo.");
    }

    @Test
    void shouldReturn422WhenCurrencyIsMissing() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": 50.00
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "currency", "A moeda é obrigatória.");
    }

    @Test
    void shouldReturn422WhenCurrencyIsBlank() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": 50.00,
                  "currency": " "
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "currency", "A moeda é obrigatória.");
    }

    @Test
    void shouldReturn422WhenCurrencyHasInvalidFormat() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": 50.00,
                  "currency": "BR"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "currency", "A moeda deve usar o código ISO 4217 de três letras.");
    }

    @Test
    void shouldReturn422WhenCurrencyIsLowercase() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": 50.00,
                  "currency": "brl"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // 2. Act & 3. Assert
        assertValidationError(requestBody, "currency", "A moeda deve usar o código ISO 4217 de três letras.");
    }

    @Test
    void shouldReturn400WhenSourceAccountIdHasInvalidFormat() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "sourceAccountId": "invalid-uuid",
                  "destinationAccountId": "%s",
                  "amount": 50.00,
                  "currency": "BRL"
                }
                """.formatted(UUID.randomUUID());

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Corpo da requisição inválido"))
                .andExpect(jsonPath("$.message").value("Não foi possível ler o corpo da requisição."))
                .andExpect(jsonPath("$.path").value("/api/v1/transfers"));

        verifyNoInteractions(useCase);
    }

    private void assertValidationError(String requestBody, String fieldName, String message) throws Exception {
        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.message").value("Existem campos inválidos na requisição."))
                .andExpect(jsonPath("$.path").value("/api/v1/transfers"))
                .andExpect(jsonPath("$.errors[0].fieldName").value(fieldName))
                .andExpect(jsonPath("$.errors[0].message").value(message));

        verifyNoInteractions(useCase);
    }
}
