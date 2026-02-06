
INSERT INTO accounts (
    id,
    account_number,
    balance,
    currency,
    user_id,
    created_at
)
VALUES
    (
        1,
        'ACC123456',
        100.00,
        'EUR',
        1,
        NOW()
    );
