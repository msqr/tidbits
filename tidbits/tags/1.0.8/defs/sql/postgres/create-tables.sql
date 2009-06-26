alter table tidbit drop constraint tidbit_kind_fk;
drop table tidbit;
drop table tidbit_kind;
drop sequence hibernate_sequence;
create table tidbit (tidbitid int8 not null, data text, comment varchar(2048), kindid int8, created_by varchar(64) not null, name varchar(128) not null, modify_date timestamp with time zone, creation_date timestamp with time zone not null, primary key (tidbitid));
create table tidbit_kind (kindid int8 not null, comment varchar(2048), created_by varchar(64) not null, name varchar(128) not null, modify_date timestamp with time zone, creation_date timestamp with time zone not null, primary key (kindid));
alter table tidbit add constraint tidbit_kind_fk foreign key (kindid) references tidbit_kind;
create sequence hibernate_sequence;
