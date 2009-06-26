alter table tidbit drop constraint tidbit_kind_fk;
drop table tidbit;
drop table tidbit_kind;
drop table hibernate_unique_key;
create table tidbit (tidbitid bigint not null, data clob(255), comment varchar(2048), kindid bigint, created_by varchar(64) not null, name varchar(128) not null, modify_date timestamp, creation_date timestamp not null, primary key (tidbitid));
create table tidbit_kind (kindid bigint not null, comment varchar(2048), created_by varchar(64) not null, name varchar(128) not null, modify_date timestamp, creation_date timestamp not null, primary key (kindid));
alter table tidbit add constraint tidbit_kind_fk foreign key (kindid) references tidbit_kind;
create table hibernate_unique_key ( next_hi integer );
insert into hibernate_unique_key values ( 0 );
