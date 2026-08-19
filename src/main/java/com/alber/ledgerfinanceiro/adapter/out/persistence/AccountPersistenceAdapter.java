package com.alber.ledgerfinanceiro.adapter.out.persistence;

import com.alber.ledgerfinanceiro.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.alber.ledgerfinanceiro.adapter.out.persistence.repository.SpringDataAccountRepository;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.domain.model.Account;
import org.springframework.stereotype.Repository;

@Repository
public class AccountPersistenceAdapter implements SaveAccountPort {

    private final SpringDataAccountRepository repository;

    public AccountPersistenceAdapter(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        var newEntity = AccountPersistenceMapper.toJpaEntity(account);
        newEntity = repository.save(newEntity);

        return AccountPersistenceMapper.toDomain(newEntity);
    }
}
