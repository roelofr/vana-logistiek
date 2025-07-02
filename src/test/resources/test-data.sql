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
INSERT INTO users (name, email, password, roles, active, district_id)
VALUES ('admin', 'admin@example.com', '$2y$04$HBZf6mETXi7IxHAzeTBtOeoU6yNDazftfwuKaKTKuHTgl.iB4awNa',
        '["role:user", "role:admin"]', 1, null),
       ('user', 'user@example.com', '$2y$04$HBZf6mETXi7IxHAzeTBtOeoU6yNDazftfwuKaKTKuHTgl.iB4awNa',
        '["role:user"]', 1, (SELECT id FROM districts WHERE name = 'test-rood')),
       ('cp', 'cp@example.com', '$2y$04$HBZf6mETXi7IxHAzeTBtOeoU6yNDazftfwuKaKTKuHTgl.iB4awNa',
        '["role:user", "role:cp"]', 1, (SELECT id FROM districts WHERE name = 'test-blauw'));
