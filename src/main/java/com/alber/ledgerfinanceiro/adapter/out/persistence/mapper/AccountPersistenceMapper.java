package com.alber.ledgerfinanceiro.adapter.out.persistence.mapper;

import com.alber.ledgerfinanceiro.adapter.out.persistence.entity.AccountJpaEntity;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.Money;

import java.util.Currency;
import java.util.Objects;

public final class AccountPersistenceMapper {

    private AccountPersistenceMapper() {
    }

    public static AccountJpaEntity toJpaEntity(Account account) {
        Objects.requireNonNull(account, "A conta é obrigatória.");

        return new AccountJpaEntity(
                account.getId().id(),
                account.getStatus(),
                account.getBalance().amount(),
                account.getBalance().currency().getCurrencyCode(),
                account.getOpenedAt()
        );
    }

    public static Account toDomain(AccountJpaEntity entity) {
        Objects.requireNonNull(entity, "A entidade da conta é obrigatória.");

        return new Account(
                new AccountId(entity.getId()),
                entity.getStatus(),
                new Money(
                        entity.getBalanceAmount(),
                        Currency.getInstance(entity.getBalanceCurrency())
                ),
                entity.getOpenedAt()
        );
    }
}
