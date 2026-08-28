package com.alber.ledgerfinanceiro.adapter.in.web.exception;

import com.alber.ledgerfinanceiro.application.exceptions.ResourceNotFoundException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountClosedException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountHasBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.AccountNotBlockedException;
import com.alber.ledgerfinanceiro.domain.exceptions.CurrencyMismatchException;
import com.alber.ledgerfinanceiro.domain.exceptions.InsufficientBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.InvalidAccountBalanceException;
import com.alber.ledgerfinanceiro.domain.exceptions.InvalidTransactionAmountException;
import com.alber.ledgerfinanceiro.domain.exceptions.SameAccountTransferException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private static final String PATH = "/api/v1/transfers";

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(PATH);
    }

    @Test
    void shouldReturn422WhenCurrenciesAreIncompatible() {
        var response = exceptionHandler.handleCurrencyMismatch(
                new CurrencyMismatchException("Moedas diferentes"), request);

        assertError(response, HttpStatus.UNPROCESSABLE_ENTITY, "Moedas incompatíveis", "Moedas diferentes");
    }

    @Test
    void shouldReturn409WhenBalanceIsInsufficient() {
        var response = exceptionHandler.handleInsufficientBalance(
                new InsufficientBalanceException("Saldo insuficiente"), request);

        assertError(response, HttpStatus.CONFLICT, "Saldo insuficiente", "Saldo insuficiente");
    }

    @Test
    void shouldReturn409WhenAccountIsBlocked() {
        var response = exceptionHandler.handleAccountBlocked(
                new AccountBlockedException("Conta bloqueada"), request);

        assertError(response, HttpStatus.CONFLICT, "Conta bloqueada", "Conta bloqueada");
    }

    @Test
    void shouldReturn409WhenAccountIsClosed() {
        var response = exceptionHandler.handleAccountClosed(
                new AccountClosedException("Conta encerrada"), request);

        assertError(response, HttpStatus.CONFLICT, "Conta encerrada", "Conta encerrada");
    }

    @Test
    void shouldReturn409WhenAccountHasBalance() {
        var response = exceptionHandler.handleAccountHasBalance(
                new AccountHasBalanceException("Saldo deve estar zerado"), request);

        assertError(response, HttpStatus.CONFLICT, "Conta com saldo", "Saldo deve estar zerado");
    }

    @Test
    void shouldReturn409WhenAccountIsNotBlocked() {
        var response = exceptionHandler.handleAccountNotBlocked(
                new AccountNotBlockedException("Conta não bloqueada"), request);

        assertError(response, HttpStatus.CONFLICT, "Conta não bloqueada", "Conta não bloqueada");
    }

    @Test
    void shouldReturn422WhenTransactionAmountIsInvalid() {
        var response = exceptionHandler.handleInvalidTransactionAmount(
                new InvalidTransactionAmountException("Valor inválido"), request);

        assertError(response, HttpStatus.UNPROCESSABLE_ENTITY, "Valor da transação inválido", "Valor inválido");
    }

    @Test
    void shouldReturn422WhenAccountBalanceIsInvalid() {
        var response = exceptionHandler.handleInvalidAccountBalance(
                new InvalidAccountBalanceException("Saldo inicial negativo"), request);

        assertError(response, HttpStatus.UNPROCESSABLE_ENTITY, "Saldo da conta inválido", "Saldo inicial negativo");
    }

    @Test
    void shouldReturn422WhenTransferUsesSameAccount() {
        var response = exceptionHandler.handleSameAccountTransfer(
                new SameAccountTransferException("Contas devem ser diferentes"), request);

        assertError(response, HttpStatus.UNPROCESSABLE_ENTITY, "Transferência inválida", "Contas devem ser diferentes");
    }

    @Test
    void shouldReturn404WhenResourceIsNotFound() {
        var response = exceptionHandler.handleResourceNotFound(
                new ResourceNotFoundException("Conta não encontrada"), request);

        assertError(response, HttpStatus.NOT_FOUND, "Recurso não encontrado", "Conta não encontrada");
    }

    @Test
    void shouldReturn422WithFieldErrorsWhenRequestValidationFails() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "currency", "A moeda é obrigatória."));
        var exception = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = exceptionHandler.handleMethodArgumentNotValid(exception, request);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationError);

        var error = (ValidationError) response.getBody();
        assertEquals("Dados inválidos", error.getError());
        assertEquals(PATH, error.getPath());
        assertEquals(1, error.getErrors().size());
        assertEquals(new FieldMessage("currency", "A moeda é obrigatória."), error.getErrors().getFirst());
    }

    @Test
    void shouldReturn400WhenRequestBodyCannotBeRead() {
        var response = exceptionHandler.handleHttpMessageNotReadable(
                new HttpMessageNotReadableException(
                        "JSON inválido",
                        Mockito.mock(HttpInputMessage.class)
                ),
                request
        );

        assertError(
                response,
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido",
                "Não foi possível ler o corpo da requisição."
        );
    }

    private void assertError(
            ResponseEntity<CustomError> response,
            HttpStatus expectedStatus,
            String expectedError,
            String expectedMessage
    ) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(expectedStatus.value(), response.getBody().getStatus());
        assertEquals(expectedError, response.getBody().getError());
        assertEquals(expectedMessage, response.getBody().getMessage());
        assertEquals(PATH, response.getBody().getPath());
    }
}
