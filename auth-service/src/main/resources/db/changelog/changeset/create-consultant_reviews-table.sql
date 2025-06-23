--liquibase formatted sql
--changeset Daniil Yatsiny:6
CREATE TABLE consultant_reviews
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    consultant_id uuid NOT NULL,
    user_id uuid NOT NULL,
    rating int,
    text varchar(150) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT consultant_id_fk FOREIGN KEY (consultant_id) REFERENCES consultant(id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES _user(id)
);

--rollback drop table consultant_reviews;