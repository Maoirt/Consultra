--liquibase formatted sql
--changeset Daniil Yatsiny:2
CREATE TABLE consultant
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL UNIQUE,
    avatar_url varchar(256),
    about varchar(1000),
    experience_years int,
    city varchar(30),
    created_at TIMESTAMP,
    CONSTRAINT consultant_id_pk PRIMARY KEY (id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES _user(id)
);

--rollback drop table consultant;