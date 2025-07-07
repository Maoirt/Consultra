--liquibase formatted sql
--changeset add-profession-to-consultant-table:1
ALTER TABLE consultant ADD COLUMN profession varchar(100);

--rollback ALTER TABLE consultant DROP COLUMN profession; 