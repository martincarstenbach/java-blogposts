--liquibase formatted sql
--changeset demo01:deployment-04 failOnError:true endDelimiter:/ stripComments:false

create or replace package hit_counter_pkg as

    function increment_counter(
        p_id            sessions.session_id%type,
        p_user_agent    sessions.user_agent%type
    ) return number;

    function increment_counter(
            p_id            sessions.session_id%type
        ) return number;

end hit_counter_pkg;
/

create or replace package body hit_counter_pkg as

    procedure new_session (
        p_id            sessions.session_id%type,
        p_user_agent    sessions.user_agent%type
    ) as
    begin
        insert into sessions(
            session_id,
            user_agent
        ) values (
            p_id,
            p_user_agent
        );
    end new_session;

    function increment_counter(
        p_id            sessions.session_id%type,
        p_user_agent    sessions.user_agent%type
    ) return number
    as
        l_num_sessions  pls_integer;
        l_num_hits      pls_integer;
    begin
        select
            count(*)
        into l_num_sessions
        from
            sessions
        where
            session_id = p_id;

        if l_num_sessions = 0 then
            new_session(p_id, p_user_agent);
        end if;

        insert into hit_count(
            session_id
        ) values (
            p_id
        );

        select
            count(*)
        into
            l_num_hits
        from
            hit_count
        where
            session_id = p_id;

        return l_num_hits;
    end increment_counter;

    function increment_counter(
        p_id            sessions.session_id%type
    ) return number
    as
        l_num_hits      pls_integer;
    begin

        insert into hit_count(
            session_id
        ) values (
            p_id
        );

        select
            count(*)
        into
            l_num_hits
        from
            hit_count
        where
            session_id = p_id;

        return l_num_hits;
    end increment_counter;

end hit_counter_pkg;
/