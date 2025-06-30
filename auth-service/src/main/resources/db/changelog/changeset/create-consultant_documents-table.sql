--liquibase formatted sql
--changeset Daniil Yatsiny:7
CREATE TABLE consultant_documents
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    consultant_id uuid NOT NULL,
    name varchar(100) NOT NULL,
    type varchar(100),
    file_url varchar(256) NOT NULL,
    description varchar(150) NOT NULL,
    CONSTRAINT consultant_documents_id_pk PRIMARY KEY (id),
    CONSTRAINT consultant_id_fk FOREIGN KEY (consultant_id) REFERENCES consultant(id)
);

--rollback drop table consultant_documents;