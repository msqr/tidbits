/* SQL for upgrade Tidbits 1.0 Postgres to 1.1.
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

alter table tidbit drop constraint FKCBE8BE1E804D9E2A;
alter table tidbit drop column Hjtype;
alter table tidbit rename column Kind to kindid;
alter table tidbit rename column CreatedBy to created_by;
alter table tidbit rename column ModifyDate to modify_date;
alter table tidbit rename column CreationDate to creation_date;

alter table tidbit_kind drop column Hjtype;
alter table tidbit_kind rename column CreatedBy to created_by;
alter table tidbit_kind rename column ModifyDate to modify_date;
alter table tidbit_kind rename column CreationDate to creation_date;

alter table tidbit add constraint tidbit_kind_fk foreign key (kindid) references tidbit_kind;
