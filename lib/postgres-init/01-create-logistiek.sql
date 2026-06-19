-- 1. Create the user 'logistiek' with password 'logistiek'
-- We use IF NOT EXISTS to prevent errors if running multiple times
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'logistiek') THEN
            CREATE ROLE logistiek WITH LOGIN PASSWORD 'logistiek';
        END IF;
    END
$$;

-- 2. Create the database 'logistiek' owned by the new user
CREATE DATABASE logistiek OWNER logistiek;

-- 3. Connect to the new database to grant schema privileges
\c logistiek

-- Grant all privileges on the default public schema to the user
GRANT ALL PRIVILEGES ON SCHEMA public TO logistiek;

-- Grant all privileges on all future tables (optional but recommended)
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO logistiek;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO logistiek;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO logistiek;

-- Optional: If you want them to be able to create objects in public schema
-- (usually not needed if they own the DB, but good for explicit access)
GRANT ALL ON SCHEMA public TO logistiek;

-- Reset connection to postgres (good practice)
\c postgres
