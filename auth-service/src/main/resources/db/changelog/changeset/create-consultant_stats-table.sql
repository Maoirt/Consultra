--liquibase formatted sql
--changeset Daniil Yatsiny:8
CREATE TABLE consultant_stats
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    consultant_id uuid NOT NULL,
    consultations_count INT,
    CONSTRAINT consultant_stats_id_pk PRIMARY KEY (id),
    CONSTRAINT consultant_id_fk FOREIGN KEY (consultant_id) REFERENCES consultant(id)
);

--rollback drop table consultant_stats;