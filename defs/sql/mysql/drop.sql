ALTER TABLE permission DROP CONSTRAINT FK_permission_groupid
ALTER TABLE tidbit DROP FOREIGN KEY FK_tidbit_kindid
DROP TABLE tidbit_kind
DROP TABLE permission
DROP TABLE permission_group
DROP TABLE tidbit
