CREATE TABLE professional_profile (
    user_id UUID PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
    display_name VARCHAR(120) NOT NULL,
    phone VARCHAR(16) NOT NULL,
    bio VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT professional_profile_display_name_not_blank
        CHECK (LENGTH(TRIM(display_name)) > 0),
    CONSTRAINT professional_profile_phone_not_blank
        CHECK (LENGTH(TRIM(phone)) > 0)
);

CREATE UNIQUE INDEX uq_professional_profile_phone
    ON professional_profile(phone);
