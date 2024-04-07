ALTER TABLE clients
    ADD COLUMN balance NUMERIC(19, 2);

UPDATE clients
SET balance = 1000000
WHERE id = 1;

ALTER TABLE clients ALTER COLUMN balance SET NOT NULL;
