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

insert into tidbit_kind (KindId,hjtype,CreatedBy,Name,CreationDate)
	values (-1,'magoffin.matt.tidbits.domain.TidbitKind','sa','Account #',CURRENT_TIMESTAMP);
insert into tidbit_kind (KindId,hjtype,CreatedBy,Name,CreationDate)
	values (-2,'magoffin.matt.tidbits.domain.TidbitKind','sa','Credit Card #',CURRENT_TIMESTAMP);
insert into tidbit_kind (KindId,hjtype,CreatedBy,Name,CreationDate)
	values (-3,'magoffin.matt.tidbits.domain.TidbitKind','sa','Password / PIN',CURRENT_TIMESTAMP);
insert into tidbit_kind (KindId,hjtype,CreatedBy,Name,CreationDate)
	values (-4,'magoffin.matt.tidbits.domain.TidbitKind','sa','Serial # / License',CURRENT_TIMESTAMP);
insert into tidbit_kind (KindId,hjtype,CreatedBy,Name,CreationDate)
	values (-5,'magoffin.matt.tidbits.domain.TidbitKind','sa','Unique Name / Logon',CURRENT_TIMESTAMP);
insert into tidbit_kind (KindId,hjtype,CreatedBy,Name,CreationDate)
	values (-6,'magoffin.matt.tidbits.domain.TidbitKind','sa','URL / IP #',CURRENT_TIMESTAMP);

insert into tidbit (TidbitId,hjtype,CreatedBy,Kind,Name,Data,CreationDate) values
	(-1,'magoffin.matt.tidbits.domain.Tidbit','sa',-5,'My Bank','mybanklogon',CURRENT_TIMESTAMP);
insert into tidbit (TidbitId,hjtype,CreatedBy,Kind,Name,Data,CreationDate) values
	(-2,'magoffin.matt.tidbits.domain.Tidbit','sa',-3,'My Bank','mybankpassword',CURRENT_TIMESTAMP);
insert into tidbit (TidbitId,hjtype,CreatedBy,Kind,Name,Data,CreationDate) values
	(-3,'magoffin.matt.tidbits.domain.Tidbit','sa',-6,'My Bank','http://www.mybank.com/login',CURRENT_TIMESTAMP);
	