--liquibase formatted sql
--changeset mcb:2

insert into employee (name, role) values
    ( 'Steven King', 'President'),
    ( 'Neena Yang', 'Administration Vice President'),
    ( 'Lex Garcia', 'Administration Vice President'),
    ( 'Jennifer Whalen', 'Administration Assistant'),
    ( 'Nancy Gruenberg', 'Finance Manager'),
    ( 'Daniel Faviet', 'Accountant'),
    ( 'John Chen', 'Accountant'),
    ( 'Ismael Sciarra', 'Accountant'),
    ( 'Jose Manuel Urman', 'Accountant'),
    ( 'Luis Popp', 'Accountant');