ALTER TABLE permission DROP CONSTRAINT FK_permission_groupid
ALTER TABLE tidbit DROP CONSTRAINT FK_tidbit_kindid
DROP TABLE tidbit_kind
DROP TABLE permission
DROP TABLE permission_group
DROP TABLE tidbit
DROP SEQUENCE TIDBIT_SEQUENCE
