--liquibase formatted sql
--changeset Daniil Yatsiny:1
CREATE TABLE _user
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    email varchar(50) NOT NULL UNIQUE,
    phone varchar(20),
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    password varchar(256),
    auth_provider varchar(50),
    enabled_verification boolean DEFAULT false,
    verification_token varchar(36),
    reset_token varchar(36),
    reset_token_expiration TIMESTAMP,
    CONSTRAINT user_id_pk PRIMARY KEY (id)
);

--rollback drop table _user;