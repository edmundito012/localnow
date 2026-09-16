CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT app_user_email_lowercase CHECK (email = LOWER(email)),
    CONSTRAINT app_user_email_not_blank CHECK (LENGTH(TRIM(email)) > 0),
    CONSTRAINT app_user_password_hash_not_blank CHECK (LENGTH(TRIM(password_hash)) > 0)
);

CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT user_role_valid CHECK (role IN ('CUSTOMER', 'PROFESSIONAL', 'ADMIN'))
);

CREATE INDEX idx_user_role_role ON user_role(role);
