package com.alber.ledgerfinanceiro.adapter.out.persistence;

import com.alber.ledgerfinanceiro.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.alber.ledgerfinanceiro.adapter.out.persistence.repository.SpringDataAccountRepository;
import com.alber.ledgerfinanceiro.application.port.out.LoadAccountPort;
import com.alber.ledgerfinanceiro.application.port.out.SaveAccountPort;
import com.alber.ledgerfinanceiro.domain.model.Account;
import com.alber.ledgerfinanceiro.domain.model.AccountId;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class AccountPersistenceAdapter implements LoadAccountPort, SaveAccountPort {

    private final SpringDataAccountRepository repository;

    public AccountPersistenceAdapter(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        Objects.requireNonNull(id, "O identificador da conta é obrigatório.");

        return repository.findById(id.id())
                .map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        var newEntity = AccountPersistenceMapper.toJpaEntity(account);
        newEntity = repository.save(newEntity);

        return AccountPersistenceMapper.toDomain(newEntity);
    }
}
