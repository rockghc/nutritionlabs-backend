-- Work data of the account, filled in by the front interface "work-profile"
-- (the step after "complete-profile").
--
--   Feature looked for            -> nlabs_user_profiles.id_feature           (one value)
--   Workplace                     -> nlabs_user_profiles.workplace_name       (free text)
--   Areas of practice             -> nlabs_user_profile_practice_areas        (several values)
--   Average clients per month     -> nlabs_user_profiles.id_clients_per_month (one value)
--
-- id_feature, id_clients_per_month and id_practice_area are ids of nlabs_catalog_details,
-- kept without foreign key like the other catalog ids of nlabs_user_profiles.
-- The new columns are nullable because the row is created at sign up, before this step.
--
-- Run connected to "NutritionLabsBD" after 05_create_user_profile.sql:
--   psql -h localhost -U postgres -d NutritionLabsBD -f 06_add_work_profile.sql

SET search_path TO nutritionlabs;

ALTER TABLE nlabs_user_profiles
    ADD COLUMN id_feature           bigint,
    ADD COLUMN workplace_name       character varying(150),
    ADD COLUMN id_clients_per_month bigint;

CREATE TABLE nlabs_user_profile_practice_areas (
    id_user_profile  bigint NOT NULL,
    id_practice_area bigint NOT NULL,
    CONSTRAINT nlabs_user_profile_practice_areas_pkey PRIMARY KEY (id_user_profile, id_practice_area),
    CONSTRAINT user_profile_practice_areas_fk FOREIGN KEY (id_user_profile) REFERENCES nlabs_user_profiles (id) ON DELETE CASCADE
);
CREATE INDEX idx_nlabs_user_profile_practice_areas_area ON nlabs_user_profile_practice_areas (id_practice_area);
