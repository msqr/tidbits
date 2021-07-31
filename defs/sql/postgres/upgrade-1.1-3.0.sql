/* SQL for upgrade Tidbits 1.1 Postgres to 3.0.
 * ===================================================================
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ===================================================================
 * $Id: upgrade-1.0-1.2.sql 100 2009-05-19 03:29:09Z msqr $
 * ===================================================================
 */

CREATE TABLE permission (permissionid BIGINT NOT NULL, created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, write_access BOOLEAN NOT NULL, PRIMARY KEY (permissionid));
CREATE TABLE permission_group (groupid BIGINT NOT NULL, created_by VARCHAR(64) NOT NULL, creation_date TIMESTAMP NOT NULL, modify_date TIMESTAMP, name VARCHAR(128) NOT NULL, PRIMARY KEY (groupid));
CREATE TABLE permission_group_perms (permission_group_id BIGINT NOT NULL, permission_id BIGINT NOT NULL, PRIMARY KEY (permission_group_id, permission_id));
ALTER TABLE permission_group_perms ADD CONSTRAINT FK_permission_group_perms_permission_group_id FOREIGN KEY (permission_group_id) REFERENCES permission_group (groupid);
ALTER TABLE permission_group_perms ADD CONSTRAINT FK_permission_group_perms_permission_id FOREIGN KEY (permission_id) REFERENCES permission (groupid);
