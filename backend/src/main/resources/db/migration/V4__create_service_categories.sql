CREATE TABLE service_category (
    code VARCHAR(50) PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT service_category_code_uppercase CHECK (code = UPPER(code)),
    CONSTRAINT service_category_display_name_not_blank
        CHECK (LENGTH(TRIM(display_name)) > 0)
);

INSERT INTO service_category (code, display_name)
VALUES
    ('PLUMBING', 'Fontanería'),
    ('ELECTRICAL', 'Electricidad'),
    ('LOCKSMITH', 'Cerrajería');

CREATE TABLE professional_service_category (
    professional_user_id UUID NOT NULL
        REFERENCES professional_profile(user_id) ON DELETE CASCADE,
    category_code VARCHAR(50) NOT NULL
        REFERENCES service_category(code),
    PRIMARY KEY (professional_user_id, category_code)
);

CREATE INDEX idx_professional_service_category_code
    ON professional_service_category(category_code);
