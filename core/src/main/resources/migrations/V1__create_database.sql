create table accounts
(
    id     bigserial
        primary key,
    requisite  char(12)       not null,
    balance    numeric(19, 2) not null,
    full_name  varchar(255)   not null,
    is_blocked boolean        not null,
    deleted_at      timestamp
);
create table limit_periods
(
    name varchar(255) not null
        primary key
);
create table limits
(
    id     bigserial
        primary key,
    amount bigint       not null,
    period_id varchar(255) not null
        constraint fkn2isj08407pu4wyg6eh9l3hu3
            references limit_periods

);
create table roles
(
    id   serial
        primary key,
    name varchar(50)
);
create table clients
(
    id        bigserial
        primary key,
    full_name varchar(255) not null,
    login     varchar(255) not null
        constraint uk_riyp540u0yca4mqdvwmf7vmbv
            unique,
    password  varchar(255) not null,
    limit_id  bigint
        constraint fksdbuss46ab11wjqwaqi06gp1q
            references limits,
    role_id   integer      not null
        constraint fkhhgglap711nuvuljv0rqh2wun
            references roles
);


create table payment_statuses
(
    name varchar(255) not null
        primary key
);

create table permissions
(
    id          bigserial
        primary key,
    description varchar(255) not null,
    name        varchar(255) not null
        constraint uk_pnvtwliis6p05pn6i3ndjrqt2
            unique
);

create table roles_permissions
(
    role_id       integer not null
        constraint fkqi9odri6c1o81vjox54eedwyh
            references roles,
    permission_id bigint  not null
        constraint fkbx9r9uw77p58gsq4mus0mec0o
            references permissions,
    primary key (role_id, permission_id)
);

create table services
(
    id            bigserial
        primary key,
    is_cancelable boolean      not null,
    max           numeric(19, 2),
    min           numeric(19, 2),
    name          varchar(255) not null,
    logo          varchar(255) not null

);

create table payments
(
    id           bigserial
        primary key,
    amount       numeric(19, 2) not null,
    created_at   timestamp      not null,
    paid_at      timestamp,
    rollback_date timestamp,
    external_id   bigint,


        requisite_id bigint not null
        constraint fk913av0w12dx1dyoepqyh9m3b0
            references accounts,
    client_id    bigint         not null
        constraint fk7q4i5uacsdt9cx0xx77nwmaso
            references clients,
    status_id    varchar(255)   not null
        constraint fkxjtgj097cq4v3dqs1dc53m91
            references payment_statuses,
    service_id   bigint         not null
        constraint fkopw6nnotm6p3pkyb4iuco4h0y
            references services

);


create table payment_logs
(
    id                 bigserial
        primary key,
    action             varchar(255) not null,
    datetime           timestamp    not null,
    details            varchar(255),
    request_parameters varchar(255),
    response           varchar(255),
    payment_id         bigint
        constraint fks6n0xqxey52uk7fmwepq6vh47
            references payments
);

create table users
(
    id       bigserial
        primary key,
    email    varchar(50)
        constraint uk6dotkott2kjsp8vw4d0m25fb7
            unique,
    password varchar(120),
    username varchar(20)
        constraint ukr43af9ap4edm43mmtq01oddj6
            unique
);

create table users_roles
(
    user_id bigint  not null
        constraint fk2o0jvgh89lemvvo17cbqvdxaa
            references users,
    role_id integer not null
        constraint fkj6m8fwv7oqv74fcehir1a9ffy
            references roles,
    primary key (user_id, role_id)
);

