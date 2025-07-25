--liquibase formatted sql
--changeset chat-message-table:1
CREATE TABLE chat_message (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    from_id uuid NOT NULL,
    to_id uuid NOT NULL,
    content varchar(2000) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    chat_id VARCHAR(100) NOT NULL
);

--rollback DROP TABLE chat_message; 