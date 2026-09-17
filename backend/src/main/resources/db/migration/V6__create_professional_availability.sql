ALTER TABLE professional_profile
    ADD COLUMN time_zone VARCHAR(50);

CREATE TABLE professional_availability (
    id UUID PRIMARY KEY,
    professional_user_id UUID NOT NULL
        REFERENCES professional_profile(user_id) ON DELETE CASCADE,
    day_of_week SMALLINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT professional_availability_day_valid
        CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT professional_availability_time_valid
        CHECK (start_time < end_time),
    CONSTRAINT professional_availability_unique_slot
        UNIQUE (professional_user_id, day_of_week, start_time, end_time)
);

CREATE INDEX idx_professional_availability_lookup
    ON professional_availability(professional_user_id, day_of_week, start_time, end_time);
