ALTER TABLE professional_profile
    ADD COLUMN location GEOGRAPHY(POINT, 4326),
    ADD COLUMN service_radius_m INTEGER;

ALTER TABLE professional_profile
    ADD CONSTRAINT professional_profile_service_area_complete
        CHECK (
            (location IS NULL AND service_radius_m IS NULL)
            OR
            (location IS NOT NULL AND service_radius_m IS NOT NULL)
        ),
    ADD CONSTRAINT professional_profile_service_radius_valid
        CHECK (
            service_radius_m IS NULL
            OR service_radius_m BETWEEN 1000 AND 100000
        );

CREATE INDEX idx_professional_profile_location
    ON professional_profile
    USING GIST(location);
