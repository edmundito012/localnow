CREATE TABLE service_request (
    id UUID PRIMARY KEY,
    customer_user_id UUID NOT NULL
        REFERENCES app_user(id),
    category_code VARCHAR(50) NOT NULL
        REFERENCES service_category(code),
    description VARCHAR(1000) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    location GEOGRAPHY(POINT, 4326)
        GENERATED ALWAYS AS (
            CAST(
                ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)
                AS geography
            )
        ) STORED,
    requested_at TIMESTAMPTZ NOT NULL,
    budget_amount_cents BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT service_request_description_not_blank
        CHECK (LENGTH(TRIM(description)) > 0),
    CONSTRAINT service_request_latitude_valid
        CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT service_request_longitude_valid
        CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT service_request_budget_positive
        CHECK (budget_amount_cents > 0),
    CONSTRAINT service_request_currency_uppercase
        CHECK (currency = UPPER(currency)),
    CONSTRAINT service_request_status_valid
        CHECK (status IN ('OPEN', 'MATCHED', 'CANCELLED', 'COMPLETED'))
);

CREATE INDEX idx_service_request_location
    ON service_request USING GIST(location);

CREATE INDEX idx_service_request_customer_status
    ON service_request(customer_user_id, status);

CREATE INDEX idx_service_request_category_requested_at
    ON service_request(category_code, requested_at);
