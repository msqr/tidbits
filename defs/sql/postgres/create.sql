CREATE TABLE tidbit_kind (kindid BIGINT NOT NULL, comment VARCHAR(2048), created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, PRIMARY KEY (kindid));
CREATE TABLE permission (permissionid BIGINT NOT NULL, created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, write_access BOOLEAN NOT NULL, PRIMARY KEY (permissionid));
CREATE TABLE permission_group (groupid BIGINT NOT NULL, created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, PRIMARY KEY (groupid));
CREATE TABLE tidbit (tidbitid BIGINT NOT NULL, comment VARCHAR(2048), created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, data VARCHAR(32672), modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, kindid BIGINT NOT NULL, PRIMARY KEY (tidbitid));
CREATE TABLE permission_group_perms (permission_group_id BIGINT NOT NULL, permission_id BIGINT NOT NULL, PRIMARY KEY (permission_group_id, permission_id));
ALTER TABLE tidbit ADD CONSTRAINT FK_tidbit_kindid FOREIGN KEY (kindid) REFERENCES tidbit_kind (kindid);
ALTER TABLE permission_group_perms ADD CONSTRAINT FK_permission_group_perms_permission_group_id FOREIGN KEY (permission_group_id) REFERENCES permission_group (groupid);
ALTER TABLE permission_group_perms ADD CONSTRAINT FK_permission_group_perms_permission_id FOREIGN KEY (permission_id) REFERENCES permission (permissionid);
CREATE SEQUENCE TIDBIT_SEQUENCE START WITH 1;
