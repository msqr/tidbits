/* SQL for Derby to populate some default Tidbits data.
 * 
 * This script is used to populate the Tidbits Live Demo database.
 * Connect to Derby as sa/manager (add ;create=true to JDBC URL
 * for first-time ues).
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
 * $Id$
 * ===================================================================
 */

insert into tidbit_kind (kindid,created_by,name,creation_date)
	values (-1,'app','Account #',CURRENT_TIMESTAMP);
insert into tidbit_kind (kindid,created_by,name,creation_date)
	values (-2,'app','Credit Card #',CURRENT_TIMESTAMP);
insert into tidbit_kind (kindid,created_by,name,creation_date)
	values (-3,'app','Password / PIN',CURRENT_TIMESTAMP);
insert into tidbit_kind (kindid,created_by,name,creation_date)
	values (-4,'app','Serial # / License',CURRENT_TIMESTAMP);
insert into tidbit_kind (kindid,created_by,name,creation_date)
	values (-5,'app','Unique Name / Logon',CURRENT_TIMESTAMP);
insert into tidbit_kind (kindid,created_by,name,creation_date)
	values (-6,'app','URL / IP #',CURRENT_TIMESTAMP);

insert into tidbit (tidbitid,created_by,kindid,name,data,creation_date) values
	(-1,'app',-5,'My Bank','mybanklogon',CURRENT_TIMESTAMP);
insert into tidbit (tidbitid,created_by,kindid,name,data,creation_date) values
	(-2,'app',-3,'My Bank','mybankpassword',CURRENT_TIMESTAMP);
insert into tidbit (tidbitid,created_by,kindid,name,data,creation_date) values
	(-3,'app',-6,'My Bank','http://www.mybank.com/login',CURRENT_TIMESTAMP);
	