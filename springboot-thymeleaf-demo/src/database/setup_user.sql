/*
    Set up a demo account for this spring boot application.

    Must be connected as a privileged user (SYSTEM, SYS, etc) to
    create the account. In case of a CDB, create the account in a
    suitable PDB. Adjust default and temporary tablespace to match
    your environment
 */

create user "SPRINGDEMO"
identified by values 'S:0B22F5andlotsmorecharacters'
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