--liquibase formatted sql
--changeset Daniil Yatsiny:5
CREATE TABLE consultant_services
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    consultant_id uuid NOT NULL,
    name varchar(100) NOT NULL,
    description varchar(150) NOT NULL,
    price int NOT NULL,
    CONSTRAINT consultant_services_id_pk PRIMARY KEY (id),
    CONSTRAINT consultant_id_fk FOREIGN KEY (consultant_id) REFERENCES consultant(id)
 );

--rollback drop table consultant_services;