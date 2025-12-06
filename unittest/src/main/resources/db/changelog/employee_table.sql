--liquibase formatted sql
--changeset mcb:1

CREATE TABLE employee (
    id   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(128),
    role VARCHAR2(128)
);
