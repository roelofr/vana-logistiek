DELETE
FROM districts
WHERE ID IN (5001, 5002, 5003);

--
-- Insert districts
--
INSERT INTO districts (id, name, colour)
VALUES (5001, 'test-rood', 'red'),
       (5002, 'test-blauw', 'blue'),
       (5003, 'test-groen', 'green');

--
-- Remove any seeded data
--
TRUNCATE TABLE vendors;
TRUNCATE TABLE users;

--
-- Insert vendors
--
INSERT INTO vendors (id, name, number, district_id)
VALUES (1, '100a', 'Test One', 5001),
       (2, '1100a', 'Test Two', 5002),
       (3, '1202', 'Test Three', 5002);

--
-- Insert dummy users
--
INSERT INTO users (id, name, email, password, roles, district_id)
VALUES (1,
        'admin',
        'admin@exampole.com',
        '$2a$04$3axp5uQxkpN2ZuHOgn9jGu/i09l2Nm80gkG04t2c01.9g.cplLA7m',
        '[
          "admin"
        ]',
        null);
