CREATE TABLE transfers (
    id UUID NOT NULL,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_transfers PRIMARY KEY (id),
    CONSTRAINT ck_transfers_different_accounts
        CHECK (source_account_id <> destination_account_id),
    CONSTRAINT ck_transfers_positive_amount
        CHECK (amount > 0)
);
