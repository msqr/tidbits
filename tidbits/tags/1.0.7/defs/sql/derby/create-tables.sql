alter table tidbit drop constraint FKCBE8BE1E804D9E2A;
drop table tidbit;
drop table tidbit_kind;
drop table hibernate_unique_key;
create table tidbit (TidbitId bigint not null, Hjtype varchar(255) not null, Data clob(255), Comment varchar(2048), Kind bigint, CreatedBy varchar(64) not null, Name varchar(128) not null, ModifyDate timestamp, CreationDate timestamp not null, primary key (TidbitId));
create table tidbit_kind (KindId bigint not null, Hjtype varchar(255) not null, Comment varchar(2048), CreatedBy varchar(64) not null, Name varchar(128) not null, ModifyDate timestamp, CreationDate timestamp not null, primary key (KindId));
alter table tidbit add constraint FKCBE8BE1E804D9E2A foreign key (Kind) references tidbit_kind;
create table hibernate_unique_key ( next_hi integer );
insert into hibernate_unique_key values ( 0 );
