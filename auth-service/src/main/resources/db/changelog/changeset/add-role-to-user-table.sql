--liquibase formatted sql
--changeset admin:add-role-to-user-table
ALTER TABLE _user ADD COLUMN role VARCHAR(32) DEFAULT 'USER';
--rollback ALTER TABLE _user DROP COLUMN role; 