--
-- Delete any seeded data
--
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
-- Insert districts
--
INSERT INTO districts (name, colour)
VALUES ('test-rood', 'red'),
       ('test-blauw', 'blue'),
       ('test-groen', 'green');


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
INSERT INTO users (name, email, provider_id, roles, active, district_id)
VALUES ('admin', 'admin@example.com', 'admin@example.com',
        '["role:user", "role:admin"]', 1, null),
       ('user', 'user@example.com', 'user@example.com',
        '["role:user"]', 1, (SELECT id FROM districts WHERE name = 'test-rood')),
       ('cp', 'cp@example.com', 'cp@example.com',
        '["role:user", "role:cp"]', 1, (SELECT id FROM districts WHERE name = 'test-blauw')),
       ('new-user', 'new-user@example.com', 'provider-new-user', '[]', 1, null),
       ('frozen', 'mr-freeze@example.com', 'provider-frozen', '[]', 0, null),
       ('frozen-for-activation', 'frozen-for-activation@example.com', null, '[]', 0, null);
