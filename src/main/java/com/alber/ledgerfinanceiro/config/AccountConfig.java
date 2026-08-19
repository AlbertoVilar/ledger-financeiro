package com.alber.ledgerfinanceiro.config;

import com.alber.ledgerfinanceiro.application.port.in.CreateAccountUseCase;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.application.usecase.CreateAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public CreateAccountUseCase create(SaveAccountPort saveAccountPort) {
        return new CreateAccountService(saveAccountPort);
    }
}
