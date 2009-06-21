alter table tidbit drop foreign key FKCBE8BE1E804D9E2A;
drop table if exists tidbit;
drop table if exists tidbit_kind;
create table tidbit (TidbitId bigint not null auto_increment, Hjtype varchar(255) not null, Data text, Comment text, Kind bigint, CreatedBy varchar(64) not null, Name varchar(128) not null, ModifyDate timestamp, CreationDate timestamp not null, primary key (TidbitId));
create table tidbit_kind (KindId bigint not null auto_increment, Hjtype varchar(255) not null, Comment text, CreatedBy varchar(64) not null, Name varchar(128) not null, ModifyDate timestamp, CreationDate timestamp not null, primary key (KindId));
alter table tidbit add index FKCBE8BE1E804D9E2A (Kind), add constraint FKCBE8BE1E804D9E2A foreign key (Kind) references tidbit_kind (KindId);
