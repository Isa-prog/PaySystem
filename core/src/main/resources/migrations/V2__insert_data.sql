-- V2__insert_data.sql
-- Описание миграции: Вставка данных в таблицу roles

-- Описание миграции ROLES
INSERT INTO roles(name) VALUES('ROLE_SUPER_ADMIN');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
INSERT INTO roles(name) VALUES('ROLE_INCIDENT_MANAGER');
INSERT INTO roles(name) VALUES('ROLE_MANAGER');
INSERT INTO roles(name) VALUES('ROLE_CLIENT');

-- Описание миграции super-admin
DO $$
    DECLARE
        last_user_id INTEGER;

    BEGIN
        -- Создание пользователя и получение ID
        INSERT INTO users (email, password, username)
        VALUES ('super-admin@gmail.com', '$2a$12$Qll7eJxTBpa5bjVNpSMOd.9USpQ9nDRYX8/j6IT2ZMARdqxg04sxG','super-admin')
        RETURNING id INTO last_user_id;

        -- Присвоение роли пользователю
        INSERT INTO users_roles (user_id, role_id)
        VALUES (last_user_id, 1);
    END $$;

-- Описание миграции payment_statuses
INSERT INTO payment_statuses(name) VALUES('PAYMENT_SUCCESS');
INSERT INTO payment_statuses(name) VALUES('PAYMENT_DENIED');
INSERT INTO payment_statuses(name) VALUES('PAYMENT_IN_PROGRESS');
INSERT INTO payment_statuses(name) VALUES('PAYMENT_CANCELLED');
INSERT INTO payment_statuses(name) VALUES('CANCEL_IN_PROGRESS');
INSERT INTO payment_statuses(name) VALUES('CANCEL_DENIED');

-- Описание миграции PERMISSIONS
-- Платежи
INSERT INTO permissions(description, name) VALUES('Просмотр платежа','payment.read');  --1
INSERT INTO permissions(description, name) VALUES('Создание платежа','payment.create');  --2
INSERT INTO permissions(description, name) VALUES('Отмена платежа','payment.cancel');  --3
INSERT INTO permissions(description, name) VALUES('Смена статуса платежа','payment.status.change');  --4

-- Аккаунты
INSERT INTO permissions(description, name) VALUES('Просмотр аккаунта','account.read');  --5
INSERT INTO permissions(description, name) VALUES('Создание аккаунта','account.create');  --6
INSERT INTO permissions(description, name) VALUES('Удаление аккаунта','account.delete');  --7

-- Пользователи
INSERT INTO permissions(description, name) VALUES('Просмотр пользователя','user.read');  --8
INSERT INTO permissions(description, name) VALUES('Создание пользователя','user.create');  --9
INSERT INTO permissions(description, name) VALUES('Удаление пользователя','user.delete');  --10

-- Лимиты
INSERT INTO permissions(description, name) VALUES('Просмотр лимита','limit.read');  --11
INSERT INTO permissions(description, name) VALUES('Создание лимита','limit.create');  --12
INSERT INTO permissions(description, name) VALUES('Удаление лимита','limit.delete');  --13

-- Лого
INSERT INTO permissions(description, name) VALUES('Просмотр лого','logo.read');  --14
INSERT INTO permissions(description, name) VALUES('Создание лого','logo.create');  --15
INSERT INTO permissions(description, name) VALUES('Удаление лого','logo.delete');  --16

-- Клиенты
INSERT INTO permissions(description, name) VALUES('Просмотр клиента','client.read');  --17
INSERT INTO permissions(description, name) VALUES('Создание клиента','client.create');  --18
INSERT INTO permissions(description, name) VALUES('Удаление клиента','client.delete');  --19

-- Услуги
INSERT INTO permissions(description, name) VALUES('Просмотр услуги','services.read');  --20
INSERT INTO permissions(description, name) VALUES('Создание услуги','services.create');  --21
INSERT INTO permissions(description, name) VALUES('Удаление услуги','services.delete');  --22

INSERT INTO permissions(description, name) VALUES('Получение статуса платежа','payment.status.get');  --23

INSERT INTO permissions(description, name) VALUES('Создание админов','admin.create');  --24
INSERT INTO permissions(description, name) VALUES('Создание админов','admin.delete');  --25

-- Описание миграции ROLES-PERMISSIONS
-- 'ROLE_SUPER_ADMIN' со всеми разрешениями
INSERT INTO roles_permissions (role_id, permission_id)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
    (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12),
    (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18),
     (1, 19), (1, 20), (1, 21), (1, 22), (1, 24), (1, 25);


-- 'ROLE_ADMIN'
INSERT INTO roles_permissions (role_id, permission_id)
VALUES
    (2, 1), (2, 2), (2, 5), (2, 6),
    (2, 7), (2, 8), (2, 9), (2, 10), (2, 11), (2, 12),
    (2, 13), (2, 14), (2, 15), (2, 16), (2, 17), (2, 18),
     (2, 19), (2, 20), (2, 21), (2, 22);


-- 'ROLE_INCIDENT_MANAGER'
INSERT INTO roles_permissions (role_id, permission_id)
VALUES
    (3, 1),  (3, 3), (3, 4),
    (3, 5), (3, 11), (3, 14), (3, 17), (3, 20);


-- 'ROLE_MANAGER'
INSERT INTO roles_permissions (role_id, permission_id)
VALUES
    (4, 1),  (4, 5), (4, 11), (4, 14), (4, 17), (4, 20) ;


-- 'ROLE_CLIENT'
INSERT INTO roles_permissions (role_id, permission_id)
VALUES
    (5, 1), (5, 2), (5, 3), (5, 5), (5, 23);

INSERT INTO limit_periods(name)
VALUES ('Minute'),
       ('Day'),
       ('Week'),
       ('Month'),
       ('Year');

INSERT INTO limits(amount, period_id)
VALUES (5, 'Minute');

INSERT INTO clients(full_name, login, password, limit_id, role_id)
VALUES ('Nur Telecom', 'nurTelecomTest1', '$2a$10$wScCezWbw0GlYa606P9GquBcXoaandeYIGQuLWRWJD4Lmn4p55oRy', 1, 5);


INSERT INTO accounts(requisite, balance, full_name, is_blocked)
VALUES ('996558974506', 1000, 'Tabyldieva Shoola', false),
       ('996779345675', 500, 'Alkanov Akmal', false),
       ('996708945678', 400, 'Konokkazyev Aman', true),
       ('996554896535', 420, 'Pogorelchenko Ludmila', false);

INSERT INTO services(is_cancelable, max, min, name, logo)
VALUES (true, 2000, 1000, 'Мобильная связь', 'https://res.cloudinary.com/dzr0hcifv/image/upload/v1709651550/1f559687-9fd6-4869-ac63-e1f28e865b1a.png');

INSERT INTO payments(amount, created_at, paid_at, requisite_id, rollback_date, external_id, client_id, status_id, service_id)
    VALUES(100,'2023-01-13T17:09:42.411','2023-01-13T17:09:42.412', 1,'2023-01-13T17:09:42.411', 1, 1, 'PAYMENT_SUCCESS', 1),
          (50,'2023-10-15T17:10:42.411','2023-10-15T17:10:42.412', 2,'2023-01-13T17:09:42.411', 2, 1, 'PAYMENT_IN_PROGRESS', 1),
          (20,'2023-12-03T17:09:42.411','2023-12-03T17:09:42.412', 4,'2023-01-13T17:09:42.411', 3, 1, 'PAYMENT_CANCELLED', 1),
            (40,'2023-12-03T17:09:42.411','2023-12-03T17:09:42.412', 4,'2023-01-13T17:09:42.411', 4, 1, 'PAYMENT_CANCELLED', 1);






