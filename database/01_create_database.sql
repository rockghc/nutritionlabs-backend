-- Creates the database that hosts the merged NutritionLabs schema.
-- Run connected to the "postgres" database as the "postgres" user:
--   psql -h localhost -U postgres -d postgres -f 01_create_database.sql

CREATE DATABASE "NutritionLabsBD"
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    TEMPLATE = template0;
