--liquibase formatted sql
--changeset Daniil Yatsiny:3
CREATE TABLE consultant_specialization
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name varchar(35) NOT NULL UNIQUE,
    CONSTRAINT consultant_specialization_id_pk PRIMARY KEY (id)
);

--rollback drop table consultant_specialization;