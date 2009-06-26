alter table tidbit drop foreign key tidbit_kind_fk;
drop table if exists tidbit;
drop table if exists tidbit_kind;
create table tidbit (tidbitid bigint not null auto_increment, data longtext, comment longtext, kindid bigint, created_by varchar(64) not null, name varchar(128) not null, modify_date timestamp, creation_date timestamp not null, primary key (tidbitid));
create table tidbit_kind (kindid bigint not null auto_increment, comment longtext, created_by varchar(64) not null, name varchar(128) not null, modify_date timestamp, creation_date timestamp not null, primary key (kindid));
alter table tidbit add index tidbit_kind_fk (kindid), add constraint tidbit_kind_fk foreign key (kindid) references tidbit_kind (kindid);
