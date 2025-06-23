--liquibase formatted sql
--changeset Daniil Yatsiny:4
CREATE TABLE consultant_to_specialization
(
    consultant_id uuid NOT NULL,
    specialization_id uuid NOT NULL,
    CONSTRAINT consultant_id_fk FOREIGN KEY (consultant_id) REFERENCES consultant(id),
    CONSTRAINT specialization_id_fk FOREIGN KEY (specialization_id) REFERENCES consultant_specialization(id),
    CONSTRAINT consultant_to_specialization_pk PRIMARY KEY (consultant_id, specialization_id)
);

--rollback drop table consultant_to_specialization;