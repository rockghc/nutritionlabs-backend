-- Catalogs of the front interface "complete-profile" that were hard coded in the front:
--
--   Phone prefix -> catalog 13 "dataDialingCode" (detail label = value stored in nlabs_user_profiles.phone_prefix)
--   Profession   -> catalog 20 "professions"     (detail id    = value stored in nlabs_user_profiles.id_profession)
--
-- The dialing codes already existed (Colombia, Canada, United States) with the label without "+". The label is
-- changed to the format of nlabs_user_profiles.phone_prefix ('^\+[0-9]{1,4}$') and the missing codes are added,
-- each one linked to its country (catalog 12) like the existing ones (nlabs_catalog_link, DIALING_CODE).
--
-- The ids of nlabs_catalogs and nlabs_catalog_details are not generated, so they are fixed here and
-- continue after the migrated data (catalogs 1-19, details 1-419).
-- The script can be run again: the rows that already exist are skipped.
--
-- Run connected to "NutritionLabsBD":
--   psql -h localhost -U postgres -d NutritionLabsBD -f 07_insert_phone_prefix_and_profession_catalogs.sql

SET search_path TO nutritionlabs;

BEGIN;

-- Phone prefixes (catalog 13).
UPDATE nlabs_catalog_details
SET label = '+' || label
WHERE id_catalog = 13 AND label NOT LIKE '+%';

INSERT INTO nlabs_catalog_details (id, id_catalog, name, label) VALUES
    (420, 13, 'dialing_code_spain',     '+34'),
    (421, 13, 'dialing_code_peru',      '+51'),
    (422, 13, 'dialing_code_mexico',    '+52'),
    (423, 13, 'dialing_code_argentina', '+54'),
    (424, 13, 'dialing_code_chile',     '+56'),
    (425, 13, 'dialing_code_ecuador',   '+593')
ON CONFLICT (id) DO NOTHING;

-- Dialing code -> country (ids of catalog 12).
INSERT INTO nlabs_catalog_link (id_catalog, id_catalog_link, link_type) VALUES
    (420, 298, 'DIALING_CODE'),
    (421, 263, 'DIALING_CODE'),
    (422, 232, 'DIALING_CODE'),
    (423, 102, 'DIALING_CODE'),
    (424, 136, 'DIALING_CODE'),
    (425, 154, 'DIALING_CODE')
ON CONFLICT (id_catalog, id_catalog_link) DO NOTHING;

-- Professions (catalog 20).
INSERT INTO nlabs_catalogs (id, name, label, catalog_type) VALUES
    (20, 'professions', 'Professions', 'PROFESSIONS')
ON CONFLICT (id) DO NOTHING;

INSERT INTO nlabs_catalog_details (id, id_catalog, name, label) VALUES
    (426, 20, 'nutritionist',     'Nutritionist'),
    (427, 20, 'dietitian',        'Dietitian'),
    (428, 20, 'physician',        'Physician'),
    (429, 20, 'personal-trainer', 'Personal trainer'),
    (430, 20, 'student',          'Student'),
    (431, 20, 'other',            'Other')
ON CONFLICT (id) DO NOTHING;

COMMIT;
