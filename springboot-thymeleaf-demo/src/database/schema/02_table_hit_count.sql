--liquibase formatted sql
--changeset demo01:deployment-02 failOnError:true

create table hit_count (
    session_id varchar2(36) not null,
    constraint fk_sessions_hits
        foreign key (session_id)
        references sessions,
    hit_time timestamp default systimestamp
);