--liquibase formatted sql
--changeset demo01:deployment-03 failOnError:true

create index i_hit_count_sessions
on hit_count(session_id);