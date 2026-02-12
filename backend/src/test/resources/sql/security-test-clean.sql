-- Order matters because of FK constraints
DELETE FROM refresh_tokens;
DELETE FROM transactions;
DELETE FROM accounts;
DELETE FROM users;
