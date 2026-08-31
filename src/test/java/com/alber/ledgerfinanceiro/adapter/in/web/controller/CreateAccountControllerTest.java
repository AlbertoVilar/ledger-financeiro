package com.alber.ledgerfinanceiro.adapter.in.web.controller;

import com.alber.ledgerfinanceiro.adapter.in.web.dto.CreateAccountRequest;
import com.alber.ledgerfinanceiro.application.port.in.CreateAccountCommand;
import com.alber.ledgerfinanceiro.application.port.in.CreateAccountUseCase;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Currency;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreateAccountController.class)
class CreateAccountControllerTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateAccountUseCase useCase;

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        // 1. Arrange
        var request = new CreateAccountRequest("BRL");
        var requestBody = objectMapper.writeValueAsString(request);
        var account = Account.create(new AccountId(UUID.randomUUID()), BRL);

        when(useCase.create(new CreateAccountCommand(BRL))).thenReturn(account);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(account.getId().id().toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.currency").value("BRL"));

        verify(useCase).create(new CreateAccountCommand(BRL));
    }

    @Test
    void shouldNormalizeCurrencyToUppercaseBeforeCreatingAccount() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "currency": "brl"
                }
                """;
        var account = Account.create(new AccountId(UUID.randomUUID()), BRL);

        when(useCase.create(new CreateAccountCommand(BRL))).thenReturn(account);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("BRL"));

        verify(useCase).create(new CreateAccountCommand(BRL));
    }

    @Test
    void shouldReturn422WhenCurrencyIsBlank() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "currency": " "
                }
                """;

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.message").value("Existem campos inválidos na requisição."))
                .andExpect(jsonPath("$.path").value("/api/v1/accounts"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("currency"))
                .andExpect(jsonPath("$.errors[0].message").value("A moeda é obrigatória."));

        verifyNoInteractions(useCase);
    }

    @Test
    void shouldReturn422WhenCurrencyIsMissing() throws Exception {
        // 1. Arrange
        String requestBody = """
                {}
                """;

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.errors[0].fieldName").value("currency"))
                .andExpect(jsonPath("$.errors[0].message").value("A moeda é obrigatória."));

        verifyNoInteractions(useCase);
    }

    @Test
    void shouldReturn422WhenCurrencyHasInvalidFormat() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "currency": "BR"
                }
                """;

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.errors[0].fieldName").value("currency"))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("A moeda deve usar o código ISO 4217 de três letras."));

        verifyNoInteractions(useCase);
    }

    @Test
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        // 1. Arrange
        String requestBody = """
                {
                  "currency":
                }
                """;

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Corpo da requisição inválido"))
                .andExpect(jsonPath("$.message").value("Não foi possível ler o corpo da requisição."))
                .andExpect(jsonPath("$.path").value("/api/v1/accounts"));

        verifyNoInteractions(useCase);
    }
}
