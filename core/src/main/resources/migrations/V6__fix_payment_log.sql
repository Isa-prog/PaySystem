ALTER TABLE payment_logs DROP CONSTRAINT fks6n0xqxey52uk7fmwepq6vh47;

ALTER TABLE payment_logs ALTER COLUMN response TYPE varchar(2048);

ALTER TABLE payment_logs ADD CONSTRAINT fks6n0xqxey52uk7fmwepq6vh47 FOREIGN KEY (payment_id) REFERENCES payments(id);
