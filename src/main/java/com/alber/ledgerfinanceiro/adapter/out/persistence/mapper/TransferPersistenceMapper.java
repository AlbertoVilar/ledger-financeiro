package com.alber.ledgerfinanceiro.adapter.out.persistence.mapper;

import com.alber.ledgerfinanceiro.adapter.out.persistence.entity.TransferJpaEntity;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import com.alber.ledgerfinanceiro.domain.model.Money;
import com.alber.ledgerfinanceiro.domain.model.Transfer;
import com.alber.ledgerfinanceiro.domain.model.TransferId;

import java.util.Currency;
import java.util.Objects;

public final class TransferPersistenceMapper {

    private TransferPersistenceMapper() {
    }

    public static TransferJpaEntity toJpaEntity(Transfer transfer) {
        Objects.requireNonNull(transfer, "A transferência é obrigatória.");

        return new TransferJpaEntity(
                transfer.getId().id(),
                transfer.getSourceAccountId().id(),
                transfer.getDestinationAccountId().id(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency().getCurrencyCode(),
                transfer.getOccurredAt()
        );
    }

    public static Transfer toDomain(TransferJpaEntity entity) {
        Objects.requireNonNull(entity, "A entidade da transferência é obrigatória.");

        return new Transfer(
                new TransferId(entity.getId()),
                new AccountId(entity.getSourceAccountId()),
                new AccountId(entity.getDestinationAccountId()),
                new Money(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
                entity.getOccurredAt()
        );
    }
}
