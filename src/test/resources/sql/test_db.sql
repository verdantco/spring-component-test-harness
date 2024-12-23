CREATE DATABASE test_db;

CREATE SCHEMA test_schema;

CREATE TABLE IF NOT EXISTS test_schema.test_entity
(
    id uuid NOT NULL,
    message character varying COLLATE pg_catalog."default",
    CONSTRAINT test_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS test_schema.another_test_entity
(
    id uuid NOT NULL,
    status character varying COLLATE pg_catalog."default",
    CONSTRAINT another_test_entity_pkey PRIMARY KEY (id)
);
