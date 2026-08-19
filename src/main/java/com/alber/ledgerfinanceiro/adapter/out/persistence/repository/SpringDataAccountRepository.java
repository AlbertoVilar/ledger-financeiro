package com.alber.ledgerfinanceiro.adapter.out.persistence.repository;

import com.alber.ledgerfinanceiro.adapter.out.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
}
