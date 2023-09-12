/*
    Set up a demo account for this spring boot application.

    Must be connected as a privileged user (SYSTEM, SYS, etc) to
    create the account. In case of a CDB, create the account in a
    suitable PDB. Adjust default and temporary tablespace to match
    your environment
 */

create user "SPRINGDEMO"
identified by values 'S:0B22F5DEC29E77F04F374D449B6D762CEEB04295BC2235B9F3BC39E2D554;T:861821943A171A4429184F69A7D0FE274AFE4D8BBFEA6F598C93A466D9737420224973B25E0C32A6803424341B8654B6D2BA5323DA5DE5A1733BDA0A78A82729CF8B14738E5EF7BDBF1C149DA4C1323F'
default tablespace "USERS"
temporary tablespace "TEMP"
QUOTA 100m on USERS;

grant create session to springdemo;
grant create procedure to springdemo;
grant create view to springdemo;
grant create table to springdemo;
grant create sequence to springdemo;
grant create trigger to springdemo;
grant read, write on directory DATA_PUMP_DIR to springdemo;