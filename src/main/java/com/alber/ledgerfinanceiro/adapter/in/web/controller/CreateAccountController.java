package com.alber.ledgerfinanceiro.adapter.in.web.controller;

import com.alber.ledgerfinanceiro.adapter.in.web.dto.CreateAccountRequest;
import com.alber.ledgerfinanceiro.adapter.in.web.dto.CreateAccountResponse;
import com.alber.ledgerfinanceiro.adapter.in.web.mapper.CreateAccountWebMapper;
import com.alber.ledgerfinanceiro.application.port.in.CreateAccountUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class CreateAccountController {

    private final CreateAccountUseCase useCase;

    public CreateAccountController(CreateAccountUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<CreateAccountResponse> create(@RequestBody @Valid CreateAccountRequest request){

        var result = useCase.create(CreateAccountWebMapper.toCommand(request));
        var response = CreateAccountWebMapper.toResponse(result);

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
