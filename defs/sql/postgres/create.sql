CREATE TABLE tidbit_kind (kindid BIGINT NOT NULL, comment VARCHAR(2048), created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, PRIMARY KEY (kindid));
CREATE TABLE permission (permissionid BIGINT NOT NULL, created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, write_access BOOLEAN NOT NULL, groupid BIGINT NOT NULL, PRIMARY KEY (permissionid));
CREATE TABLE permission_group (groupid BIGINT NOT NULL, created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, PRIMARY KEY (groupid));
CREATE TABLE tidbit (tidbitid BIGINT NOT NULL, comment VARCHAR(2048), created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, data VARCHAR(32672), modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, kindid BIGINT NOT NULL, PRIMARY KEY (tidbitid));
ALTER TABLE permission ADD CONSTRAINT FK_permission_groupid FOREIGN KEY (groupid) REFERENCES permission_group (groupid);
ALTER TABLE tidbit ADD CONSTRAINT FK_tidbit_kindid FOREIGN KEY (kindid) REFERENCES tidbit_kind (kindid);
CREATE SEQUENCE TIDBIT_SEQUENCE START WITH 1;
