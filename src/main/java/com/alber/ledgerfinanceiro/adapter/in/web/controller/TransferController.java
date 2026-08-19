package com.alber.ledgerfinanceiro.adapter.in.web.controller;

import com.alber.ledgerfinanceiro.adapter.in.web.dto.TransferRequest;
import com.alber.ledgerfinanceiro.adapter.in.web.dto.TransferResponse;
import com.alber.ledgerfinanceiro.adapter.in.web.mapper.TransferWebMapper;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferBetweenAccountsUseCase useCase;

    public TransferController(TransferBetweenAccountsUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request) {
        var transfer = useCase.execute(TransferWebMapper.toCommand(request));
        var response = TransferWebMapper.toResponse(transfer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
