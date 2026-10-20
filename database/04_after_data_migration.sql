-- Runs after 03_migrate_data.ps1.
--   psql -h localhost -U postgres -d NutritionLabsBD -f 04_after_data_migration.sql

SET search_path TO nutritionlabs;

-- Optimistic locking columns are mapped to primitive types, so they can not be NULL.
DO $$
DECLARE
    version_column record;
BEGIN
    FOR version_column IN
        SELECT table_name
        FROM information_schema.columns
        WHERE table_schema = 'nutritionlabs' AND column_name = 'version'
    LOOP
        EXECUTE format('UPDATE nutritionlabs.%I SET version = 0 WHERE version IS NULL', version_column.table_name);
    END LOOP;
END $$;

-- Identity sequences continue after the highest migrated id.
DO $$
DECLARE
    identity_column record;
BEGIN
    FOR identity_column IN
        SELECT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema = 'nutritionlabs' AND is_identity = 'YES'
    LOOP
        EXECUTE format(
            'SELECT setval(pg_get_serial_sequence(%L, %L), COALESCE((SELECT max(%I) FROM nutritionlabs.%I), 0) + 1, false)',
            'nutritionlabs.' || identity_column.table_name, identity_column.column_name,
            identity_column.column_name, identity_column.table_name);
    END LOOP;
END $$;

-- Foreign keys were not checked while loading: report any row that breaks one.
DO $$
DECLARE
    foreign_key record;
    orphan_rows bigint;
BEGIN
    FOR foreign_key IN
        SELECT con.conname,
               con.conrelid::regclass  AS child_table,
               con.confrelid::regclass AS parent_table,
               (SELECT string_agg(format('c.%I', att.attname), ', ' ORDER BY key.ord)
                FROM unnest(con.conkey) WITH ORDINALITY AS key(attnum, ord)
                JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = key.attnum) AS child_columns,
               (SELECT string_agg(format('p.%I', att.attname), ', ' ORDER BY key.ord)
                FROM unnest(con.confkey) WITH ORDINALITY AS key(attnum, ord)
                JOIN pg_attribute att ON att.attrelid = con.confrelid AND att.attnum = key.attnum) AS parent_columns
        FROM pg_constraint con
        WHERE con.contype = 'f' AND con.connamespace = 'nutritionlabs'::regnamespace
    LOOP
        EXECUTE format(
            'SELECT count(*) FROM %s c WHERE (%s) IS NOT NULL AND NOT EXISTS (SELECT 1 FROM %s p WHERE (%s) = (%s))',
            foreign_key.child_table, foreign_key.child_columns, foreign_key.parent_table,
            foreign_key.parent_columns, foreign_key.child_columns)
        INTO orphan_rows;
        IF orphan_rows > 0 THEN
            RAISE WARNING 'Foreign key % on %: % row(s) reference missing % rows',
                foreign_key.conname, foreign_key.child_table, orphan_rows, foreign_key.parent_table;
        END IF;
    END LOOP;
END $$;

ANALYZE;
