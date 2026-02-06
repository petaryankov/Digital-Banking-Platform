-- Order matters because of FK constraints
DELETE FROM transactions;
DELETE FROM accounts;
DELETE FROM users;
