--liquibase formatted sql
--changeset admin:add-isblocked-to-user-table
ALTER TABLE _user ADD COLUMN is_blocked BOOLEAN DEFAULT FALSE;
--rollback ALTER TABLE _user DROP COLUMN is_blocked; 