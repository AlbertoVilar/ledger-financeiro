package com.alber.ledgerfinanceiro.adapter.out.persistence;

import com.alber.ledgerfinanceiro.adapter.out.persistence.mapper.TransferPersistenceMapper;
import com.alber.ledgerfinanceiro.application.port.out.SaveTransferPort;
import com.alber.ledgerfinanceiro.adapter.out.persistence.repository.SpringDataTransferRepository;
import com.alber.ledgerfinanceiro.domain.model.Transfer;
import org.springframework.stereotype.Repository;

@Repository
public  class TransferPersistenceAdapter implements SaveTransferPort {

    private final SpringDataTransferRepository repository;

    public TransferPersistenceAdapter(SpringDataTransferRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transfer save(Transfer transfer) {
        var savedEntity = repository.save(TransferPersistenceMapper.toJpaEntity(transfer));
        return TransferPersistenceMapper.toDomain(savedEntity);
    }
}
