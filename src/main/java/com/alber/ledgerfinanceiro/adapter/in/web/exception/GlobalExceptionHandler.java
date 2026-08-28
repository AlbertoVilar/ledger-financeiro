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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<CustomError> handleCurrencyMismatch(
            CurrencyMismatchException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Moedas incompatíveis",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<CustomError> handleInsufficientBalance(
            InsufficientBalanceException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                "Saldo insuficiente",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<CustomError> handleAccountBlocked(
            AccountBlockedException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                "Conta bloqueada",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountClosedException.class)
    public ResponseEntity<CustomError> handleAccountClosed(
            AccountClosedException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                "Conta encerrada",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountHasBalanceException.class)
    public ResponseEntity<CustomError> handleAccountHasBalance(
            AccountHasBalanceException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                "Conta com saldo",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountNotBlockedException.class)
    public ResponseEntity<CustomError> handleAccountNotBlocked(
            AccountNotBlockedException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                "Conta não bloqueada",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidTransactionAmountException.class)
    public ResponseEntity<CustomError> handleInvalidTransactionAmount(
            InvalidTransactionAmountException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Valor da transação inválido",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidAccountBalanceException.class)
    public ResponseEntity<CustomError> handleInvalidAccountBalance(
            InvalidAccountBalanceException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Saldo da conta inválido",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SameAccountTransferException.class)
    public ResponseEntity<CustomError> handleSameAccountTransfer(
            SameAccountTransferException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Transferência inválida",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        var status = HttpStatus.UNPROCESSABLE_ENTITY;
        var error = new ValidationError(
                Instant.now(),
                status.value(),
                "Dados inválidos",
                "Existem campos inválidos na requisição.",
                request.getRequestURI()
        );

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido",
                "Não foi possível ler o corpo da requisição.",
                request
        );
    }

    private ResponseEntity<CustomError> buildError(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request
    ) {
        var customError = new CustomError(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(customError);
    }
}
