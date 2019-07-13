CREATE TABLE tidbit_kind (kindid BIGINT NOT NULL, comment VARCHAR(2048), created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, PRIMARY KEY (kindid));
CREATE TABLE tidbit (tidbitid BIGINT NOT NULL, comment VARCHAR(2048), created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, data VARCHAR(32672), modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, kindid BIGINT NOT NULL, PRIMARY KEY (tidbitid));
ALTER TABLE tidbit ADD CONSTRAINT FK_tidbit_kindid FOREIGN KEY (kindid) REFERENCES tidbit_kind (kindid);
CREATE SEQUENCE TIDBIT_SEQUENCE START WITH 1;
