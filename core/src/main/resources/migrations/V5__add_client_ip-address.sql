ALTER TABLE clients
    ADD COLUMN ip_address TEXT;

UPDATE clients
SET ip_address = '0:0:0:0:0:0:0:1, 127.0.0.1, 192.168.1.1'
WHERE id = 1;

ALTER TABLE clients ALTER COLUMN ip_address SET NOT NULL;