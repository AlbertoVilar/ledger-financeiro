# Ledger Financeiro

Carteira digital focada em DDD, SOLID e arquitetura hexagonal.

## Primeiro recorte

`TransferirEntreContas` move saldo entre duas contas ativas, garantindo valor positivo, contas distintas, saldo suficiente, mesma moeda e lançamento contábil balanceado.

## Estrutura

- `domain`: regras de negócio sem dependência de framework.
- `application`: casos de uso e portas de entrada/saída.
- `adapter`: integrações HTTP, banco e mensageria (a implementar).

## Diagramas de referência

Os diagramas abaixo são somente material de estudo. Eles não representam código já criado no projeto.

### Arquitetura hexagonal

```mermaid
flowchart LR
    Web[Adaptador de entrada\nWeb / REST] --> In[Porta de entrada]
    In --> UseCase[Caso de uso]
    UseCase --> Domain[Domínio\nEntidades e regras]
    UseCase --> Out[Porta de saída]
    Out --> Persistence[Adaptador de saída\nPersistência]
    Config[Configuração] -. monta dependências .-> UseCase
```

### Modelo de classes inicial

```mermaid
classDiagram
    direction LR

    class Customer {
        <<Entity>>
        -CustomerId id
        -String name
        -String email
        -String document
    }

    class Account {
        <<Entity>>
        -AccountId id
        -CustomerId customerId
        -Money balance
        -AccountStatus status
        +deposit(Money amount) void
        +withdraw(Money amount) void
        +block() void
        +close() void
    }

    class CustomerId {
        <<Value Object>>
        -UUID value
    }

    class AccountId {
        <<Value Object>>
        -UUID value
    }

    class Money {
        <<Value Object>>
        -BigDecimal amount
        -Currency currency
        +add(Money other) Money
        +subtract(Money other) Money
        +isGreaterThan(Money other) boolean
        +isPositive() boolean
    }

    class AccountStatus {
        <<Enumeration>>
        ACTIVE
        BLOCKED
        CLOSED
    }

    Customer *-- CustomerId : identificado por
    Account *-- AccountId : identificado por
    Account --> CustomerId : pertence a
    Account *-- Money : mantém saldo
    Account --> AccountStatus : possui estado
```

## Executar testes

```powershell
mvn test
```
