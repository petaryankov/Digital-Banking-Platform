DELETE FROM users;

INSERT INTO users (
    id,
    email,
    full_name,
    password,
    role,
    created_at
)
VALUES
    (
        1,
        'user@test.com',
        'User Test',
        '$2a$10$Dow1lF3FZP1Qb3z5J5qLxO7m7xvYyJ8OqFZk9z5H3yMZzZcJZy1G6', -- "password"
        'USER',
        NOW()
    ),
    (
        2,
        'admin@test.com',
        'Admin Test',
        '$2a$10$Dow1lF3FZP1Qb3z5J5qLxO7m7xvYyJ8OqFZk9z5H3yMZzZcJZy1G6', -- "password"
        'ADMIN',
        NOW()
    );
