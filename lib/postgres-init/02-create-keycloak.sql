-- 1. Create the user 'keycloak' with password 'keycloak'
-- We use IF NOT EXISTS to prevent errors if running multiple times
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'keycloak') THEN
            CREATE ROLE keycloak WITH LOGIN PASSWORD 'keycloak';
        END IF;
    END
$$;

-- 2. Create the database 'keycloak' owned by the new user
CREATE DATABASE keycloak OWNER keycloak;

-- 3. Connect to the new database to grant schema privileges
\c keycloak

-- Grant all privileges on the default public schema to the user
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak;

-- Grant all privileges on all future tables (optional but recommended)
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO keycloak;

-- Optional: If you want them to be able to create objects in public schema
-- (usually not needed if they own the DB, but good for explicit access)
GRANT ALL ON SCHEMA public TO keycloak;

-- Reset connection to postgres (good practice)
\c postgres
