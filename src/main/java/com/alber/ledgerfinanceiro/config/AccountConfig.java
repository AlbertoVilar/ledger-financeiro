package com.alber.ledgerfinanceiro.config;

import com.alber.ledgerfinanceiro.application.port.in.CreateAccountUseCase;
import com.alber.ledgerfinanceiro.application.port.in.TransferBetweenAccountsUseCase;
import com.alber.ledgerfinanceiro.application.port.out.LoadAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveTransferPort;
import com.alber.ledgerfinanceiro.application.service.TransferService;
import com.alber.ledgerfinanceiro.application.service.CreateAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AccountConfig {

    @Bean
    public CreateAccountUseCase createAccountUseCase(SaveAccountPort saveAccountPort) {
        return new CreateAccountService(saveAccountPort);
    }

    @Bean
    public TransferBetweenAccountsUseCase transferBetweenAccountsUseCase(
            LoadAccountPort loadAccountPort,
            SaveAccountPort saveAccountPort,
            SaveTransferPort saveTransferPort
    ) {
        return new TransferService(loadAccountPort, saveAccountPort, saveTransferPort);
    }
}
