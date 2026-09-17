CREATE TABLE job (
    id UUID PRIMARY KEY,
    service_request_id UUID NOT NULL UNIQUE
        REFERENCES service_request(id) ON DELETE CASCADE,
    customer_user_id UUID NOT NULL
        REFERENCES app_user(id),
    professional_user_id UUID NOT NULL
        REFERENCES app_user(id),
    status VARCHAR(32) NOT NULL,
    agreed_amount_cents BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    accepted_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT job_participants_different
        CHECK (customer_user_id <> professional_user_id),
    CONSTRAINT job_amount_positive
        CHECK (agreed_amount_cents > 0),
    CONSTRAINT job_status_valid
        CHECK (status IN ('ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_job_customer
    ON job(customer_user_id, status);

CREATE INDEX idx_job_professional
    ON job(professional_user_id, status);
