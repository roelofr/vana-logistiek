--
-- Delete any seeded data
--
DELETE
FROM user_user_group
WHERE group_id IN (
    SELECT id
    FROM user_groups
    WHERE label LIKE 'test-%'
      AND is_system = true
    );

DELETE
FROM user_groups
WHERE label LIKE 'test-%'
  AND is_system = true;

DELETE
FROM districts
WHERE name LIKE 'test-%';

DELETE
FROM vendors
WHERE name LIKE 'Test %';

DELETE
FROM users
WHERE email LIKE '%@example.com';

--
-- Insert teams
--
INSERT INTO user_groups (name, label, is_system, icon)
VALUES ('test-alpha', 'test-alpha', true, 'house'),
       ('test-bravo', 'test-bravo', true, 'tree'),
       ('test-charlie', 'test-charlie', true, 'dog'),
       ('test-cp', 'test-cp', true, 'shield');

-- Assign the district IDs to varaibles
SET @test_alpha_id = (
    SELECT id
    FROM user_groups
    WHERE label = 'test-alpha');
SET @test_bravo_id = (
    SELECT id
    FROM user_groups
    WHERE label = 'test-bravo');
SET @test_charlie_id = (
    SELECT id
    FROM user_groups
    WHERE label = 'test-charlie');
SET @test_cp_id = (
    SELECT id
    FROM user_groups
    WHERE label = 'test-cp');

--
-- Insert districts
--
INSERT INTO districts (name, colour, group_id)
VALUES ('test-rood', 'red', @test_alpha_id),
       ('test-blauw', 'blue', @test_bravo_id),
       ('test-groen', 'green', @test_charlie_id);

-- Assign districts to variables too
SET @test_rood_id = (
    SELECT id
    FROM districts
    WHERE name = 'test-rood');
SET @test_blauw_id = (
    SELECT id
    FROM districts
    WHERE name = 'test-blauw');

--
-- Insert vendors
--
INSERT INTO vendors (name, number, district_id)
VALUES ('Test One', '100a', @test_rood_id),
       ('Test Two', '1100a', @test_blauw_id),
       ('Test Three', '1202', @test_blauw_id);

--
-- Insert dummy users (password = testtest)
-- Emails must map DomainHelper.EMAIL_(name) keys
-- Districts must be null or defined in DomainHelper.DISTRICT_(name)
--
INSERT INTO users (name, email, provider_id, roles)
VALUES ('admin', 'admin@example.com', 'admin@example.com', '["role:user", "role:admin"]'),
       ('user', 'user@example.com', 'user@example.com', '["role:user"]'),
       ('cp', 'cp@example.com', 'cp@example.com', '["role:user", "role:cp"]'),
       ('new-user', 'new-user@example.com', 'provider-new-user', '[]'),
       ('frozen', 'mr-freeze@example.com', 'provider-frozen', '[]'),
       ('frozen-for-activation', 'frozen-for-activation@example.com', null, '[]');

-- Make many-to-many relations
INSERT INTO user_user_group (user_id, group_id)
VALUES (SELECT id FROM users WHERE provider_id = 'user@example.com', @test_alpha_id),
       (SELECT id FROM users WHERE provider_id = 'cp@example.com', @test_bravo_id)
