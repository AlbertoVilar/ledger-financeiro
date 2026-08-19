CREATE TABLE accounts (
    id UUID          PRIMARY KEY     NOT NULL,
    status           VARCHAR(20)     NOT NULL,
    balance_amount   DECIMAL(19, 2)  NOT NULL,
    balance_currency VARCHAR(3)     NOT NULL
);
