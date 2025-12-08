--
-- Delete any seeded data
--
DELETE
FROM teams
WHERE name LIKE 'test-%';

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
INSERT INTO teams (name, icon)
VALUES ('test-alpha', 'house'),
       ('test-bravo', 'tree'),
       ('test-charlie', 'dog'),
       ('test-cp', 'shield');

--
-- Insert districts
--
INSERT INTO districts (name, colour, team_id)
VALUES ('test-rood', 'red', (SELECT id FROM teams WHERE name = 'test-alpha')),
       ('test-blauw', 'blue', (SELECT id FROM teams WHERE name = 'test-bravo')),
       ('test-groen', 'green', (SELECT id FROM teams WHERE name = 'test-charlie'));


--
-- Insert vendors
--
INSERT INTO vendors (name, number, district_id)
VALUES ('Test One', '100a', (SELECT id FROM districts WHERE name = 'test-rood')),
       ('Test Two', '1100a', (SELECT id FROM districts WHERE name = 'test-blauw')),
       ('Test Three', '1202', (SELECT id FROM districts WHERE name = 'test-blauw'));

--
-- Insert dummy users (password = testtest)
-- Emails must map DomainHelper.EMAIL_(name) keys
-- Districts must be null or defined in DomainHelper.DISTRICT_(name)
--
INSERT INTO users (name, email, provider_id, roles, team_id)
VALUES ('admin', 'admin@example.com', 'admin@example.com',
        '["role:user", "role:admin"]', null),
       ('user', 'user@example.com', 'user@example.com',
        '["role:user"]', (SELECT id FROM teams WHERE name = 'test-alpha')),
       ('cp', 'cp@example.com', 'cp@example.com',
        '["role:user", "role:cp"]', (SELECT id FROM teams WHERE name = 'test-bravo')),
       ('new-user', 'new-user@example.com', 'provider-new-user', '[]', null),
       ('frozen', 'mr-freeze@example.com', 'provider-frozen', '[]', null),
       ('frozen-for-activation', 'frozen-for-activation@example.com', '', '[]', null);
