--liquibase formatted sql
--changeset demo01:deployment-01 failOnError:true

create table sessions (
    session_id varchar2(36),
    constraint pk_sessions
        primary key (session_id),
    user_agent varchar2(255) not null,
    duration   interval day to second
);