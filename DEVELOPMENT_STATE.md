# Estado de Desenvolvimento — Ledger Financeiro

> Documento de referência do estado atual do código. Atualizado a partir da árvore do projeto em 30 de agosto de 2026.

## 1. Visão geral da arquitetura

O projeto adota arquitetura hexagonal (Ports & Adapters), mantendo o domínio livre de dependências de Spring, JPA e Jackson.

```text
adapter.in.web  ->  application.port.in  ->  application.service  ->  domain
                                             |
                                             v
                                      application.port.out
                                             |
                                             v
                                 adapter.out.persistence
```

- **Domínio (`domain`)**: entidades, Value Objects e exceções de negócio em Java puro.
- **Aplicação (`application`)**: comandos, contratos de entrada e saída e serviços que orquestram o domínio.
- **Adaptadores de entrada (`adapter.in`)**: API REST, DTOs, mappers web e tratamento de erros HTTP.
- **Adaptadores de saída (`adapter.out`)**: JPA, Spring Data, mappers de persistência e implementação das portas de saída.
- **Configuração (`config`)**: composição de dependências, fronteira transacional e auditoria JPA.

## 2. Domínio (`domain`)

### Modelo implementado

| Tipo | Classes | Responsabilidade atual |
| --- | --- | --- |
| Entidades | `Account`, `Transfer` | Representam conta e lançamento de transferência. |
| Value Objects | `Money`, `AccountId`, `TransferId` | Valor monetário imutável e identificadores baseados em UUID. |
| Enum | `AccountStatus` | Estados `ACTIVE`, `BLOCKED` e `CLOSED`. |
| Exceções de domínio | `AccountBlockedException`, `AccountClosedException`, `AccountHasBalanceException`, `AccountNotBlockedException`, `CurrencyMismatchException`, `InsufficientBalanceException`, `InvalidAccountBalanceException`, `InvalidTransactionAmountException`, `SameAccountTransferException` | Expressam violações das invariantes do negócio. |

### Regras e comportamentos implementados

#### `Account`

- `Account.create(AccountId, Currency)` abre uma conta ativa com saldo zero e define `openedAt`.
- O construtor de reconstituição exige `id`, `status`, `balance` e `openedAt` não nulos e rejeita saldo inicial negativo.
- `deposit(Money)` e `withdraw(Money)` aceitam somente valores positivos e exigem conta ativa.
- `withdraw(Money)` impede saque acima do saldo disponível.
- `block()`, `unblock()` e `close()` protegem as transições de estado.
- Uma conta somente pode ser encerrada com saldo zero; uma conta encerrada não pode ser bloqueada ou desbloqueada.

#### `Money`

- É um `record` imutável com `amount` e `currency` obrigatórios.
- Implementa `add`, `subtract`, `isPositive`, `isZero` e `isGreaterThanOrEqual`.
- Operações entre moedas distintas lançam `CurrencyMismatchException`.
- `equals` e `hashCode` consideram valores monetários equivalentes mesmo com escalas distintas de `BigDecimal` (por exemplo, `100.0` e `100.00`).

#### `Transfer`

- `Transfer.create(sourceAccountId, destinationAccountId, amount)` gera o `TransferId` e o momento de negócio `occurredAt`.
- A criação rejeita contas de origem e destino iguais e valores zero ou negativos.
- A transferência é tratada como imutável.

### Testes de domínio

Concluídos:

- `AccountTest`;
- `MoneyTest`;
- `AccountIdTest`;
- `TransferTest`;
- `TransferIdTest`.

## 3. Casos de uso / Aplicação (`application`)

### Portas de entrada

- `CreateAccountUseCase` com `CreateAccountCommand`;
- `TransferBetweenAccountsUseCase` com `TransferBetweenAccountsCommand`.

### Serviços

- `CreateAccountService`: cria uma conta por meio de `Account.create(...)` e a salva pela porta de saída.
- `TransferService`: valida antecipadamente a transferência, carrega as duas contas, executa saque e depósito no domínio, salva os saldos e registra a transferência.

Os serviços não possuem anotações do Spring. A composição das dependências e a transação da transferência são responsabilidade de `AccountConfig`, que usa `TransactionTemplate`.

### Portas de saída

- `LoadAccountPort`: busca uma conta por `AccountId` e retorna `Optional<Account>`;
- `SaveAccountPort`: persiste uma `Account`;
- `SaveTransferPort`: persiste e retorna uma `Transfer`.

Ainda **não** existe `LoadTransferPort`: consultas de transferência, comprovante e extrato pertencem a uma etapa futura.

### Testes de aplicação

`TransferServiceTest` usa JUnit 5 e Mockito, com dublês para `LoadAccountPort`, `SaveAccountPort` e `SaveTransferPort`.

Cenários cobertos:

- transferência bem-sucedida;
- conta de origem não encontrada;
- conta de destino não encontrada;
- saldo insuficiente;
- conta de origem bloqueada;
- conta de destino bloqueada;
- transferência entre a mesma conta, sem chamar portas de saída.

`CreateAccountServiceTest` usa Mockito para validar a criação de conta ativa, com saldo zero e a moeda solicitada, além da delegação para `SaveAccountPort`.

## 4. Adaptadores de saída / Persistência (`adapter.out.persistence`)

### Adaptadores e repositórios

- `AccountPersistenceAdapter` implementa `LoadAccountPort` e `SaveAccountPort`.
- `TransferPersistenceAdapter` implementa `SaveTransferPort`.
- `SpringDataAccountRepository` e `SpringDataTransferRepository` estendem `JpaRepository`.

### Entidades e mappers JPA

- `AccountJpaEntity` representa a tabela `accounts`.
  - Persiste `openedAt` como fato de negócio.
  - Possui auditoria técnica `createdAt` e `updatedAt`.
- `TransferJpaEntity` representa a tabela `transfers`.
  - Persiste `occurredAt` como fato de negócio.
  - Possui `createdAt` como momento técnico em que o registro foi persistido.
- `AccountPersistenceMapper` e `TransferPersistenceMapper` convertem entre entidades JPA e modelos de domínio.

### Banco, migrações e auditoria

- Flyway está configurado com as migrações:
  - `V1__create_account_table.sql`;
  - `V2__create_transfers_table.sql`;
  - `V3__add_account_audit_columns.sql`;
  - `V4__add_transfer_created_at.sql`.
- Há seed local em `db/seed/dev-data.sql`.
- `JpaAuditingConfig` habilita `@EnableJpaAuditing`.
- `AccountJpaEntity` e `TransferJpaEntity` usam `AuditingEntityListener`; `@CreatedDate`/`@LastModifiedDate` permanecem exclusivamente na infraestrutura.

### Testes de integração concluídos

Os testes usam `@DataJpaTest` e `TestEntityManager`, com `flush()` e `clear()` para garantir que a verificação ocorre após uma ida real ao banco H2.

- `AccountRepositoryIT`
  - persiste e reconstitui uma conta por ID;
  - retorna vazio para uma conta inexistente.
- `TransferRepositoryIT`
  - persiste uma transferência pelo adaptador;
  - valida a reconstituição de seus dados e o preenchimento de `createdAt`.

O Maven Failsafe está configurado; portanto, `mvn verify` executa testes unitários e os testes `*IT`.

## 5. Adaptadores de entrada / Web (`adapter.in.web`)

### Endpoints implementados

- `POST /api/v1/accounts` em `CreateAccountController`;
- `POST /api/v1/transfers` em `TransferController`.

### DTOs e mappers

- Criação de conta: `CreateAccountRequest`, `CreateAccountResponse` e `CreateAccountWebMapper`.
- Transferência: `TransferRequest`, `TransferResponse` e `TransferWebMapper`.

Os requests usam validação Bean Validation por meio de `@Valid`. Em `CreateAccountRequest`, a moeda é normalizada com `trim()` e `toUpperCase(Locale.ROOT)` antes da validação; portanto, entradas como `"brl"` são tratadas como `"BRL"`.

### Tratamento de erros

- `GlobalExceptionHandler` converte exceções de domínio, recursos não encontrados, erros de validação e corpo JSON inválido em respostas HTTP.
- `CustomError`, `ValidationError` e `FieldMessage` padronizam a resposta de erro.
- `GlobalExceptionHandlerTest` cobre o tratamento de exceções da camada web.
- `CreateAccountControllerTest` cobre criação bem-sucedida, normalização da moeda, validação de moeda e JSON malformado.
- `TransferControllerTest` cobre criação bem-sucedida, validação de identificadores, valor e moeda, além de formatos JSON incompatíveis.

### Ainda pendente nesta camada

- endpoints para bloquear, desbloquear, encerrar e consultar contas;
- endpoint de consulta de transferência/comprovante e extrato;

## 6. Próximos passos — backlog imediato

1. Evoluir a consulta de transferências conforme necessidade real: `LoadTransferPort`, caso de uso de comprovante e adaptador correspondente.
2. Implementar consultas e operações REST restantes de `Account` (busca, bloqueio, desbloqueio e encerramento).
3. Estudar e implementar proteção de concorrência para transferências: iniciar com controle otimista usando `@Version` ou avaliar bloqueio pessimista conforme o cenário.
4. Definir estratégia de idempotência e tratamento de retry para operações financeiras antes de integrar mensageria.
5. Posteriormente, criar CI/CD no GitHub Actions como etapa de aprendizado, usando `mvn verify` como comando de validação.

## Como validar localmente

```powershell
mvn verify
```

Esse comando executa os testes unitários, gera o artefato e executa os testes de integração.
