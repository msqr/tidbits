alter table tidbit drop constraint FKCBE8BE1E804D9E2A;
drop table tidbit;
drop table tidbit_kind;
drop sequence hibernate_sequence;
create table tidbit (TidbitId int8 not null, Hjtype varchar(255) not null, Data text, Comment varchar(2048), Kind int8, CreatedBy varchar(64) not null, Name varchar(128) not null, ModifyDate timestamp with time zone, CreationDate timestamp with time zone not null, primary key (TidbitId));
create table tidbit_kind (KindId int8 not null, Hjtype varchar(255) not null, Comment varchar(2048), CreatedBy varchar(64) not null, Name varchar(128) not null, ModifyDate timestamp with time zone, CreationDate timestamp with time zone not null, primary key (KindId));
alter table tidbit add constraint FKCBE8BE1E804D9E2A foreign key (Kind) references tidbit_kind;
create sequence hibernate_sequence;
