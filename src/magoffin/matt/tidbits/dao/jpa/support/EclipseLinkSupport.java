/* ===================================================================
 * EclipseLinkSupport.java
 * 
 * Created Jun 16, 2012 3:44:08 PM
 * 
 * Copyright (c) 2012 Matt Magoffin.
 * 
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
 */

package magoffin.matt.tidbits.dao.jpa.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.database.oracle.Oracle11Platform;
import org.eclipse.persistence.platform.database.oracle.Oracle8Platform;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.springframework.stereotype.Component;

/**
 * EclipseLink implementation of {@link JPASupport}.
 * 
 * @author matt
 * @version 1.0
 */
@Component
public class EclipseLinkSupport implements JPASupport {

	private Map<String, String> drivers;

	public EclipseLinkSupport() {
		super();
		drivers = new LinkedHashMap<String, String>(10);
		drivers.put(DB2Platform.class.getName(), "DB2");
		drivers.put(DerbyPlatform.class.getName(), "Derby");
		drivers.put(MySQLPlatform.class.getName(), "MySQL");
		drivers.put(Oracle8Platform.class.getName(), "Oracle 8");
		drivers.put(Oracle9Platform.class.getName(), "Oracle 9");
		drivers.put(Oracle10Platform.class.getName(), "Oracle 10");
		drivers.put(Oracle11Platform.class.getName(), "Oracle 11");
		drivers.put(PostgreSQLPlatform.class.getName(), "PostgreSQL");
		drivers.put(SQLServerPlatform.class.getName(), "SQL Server");
		drivers = Collections.unmodifiableMap(drivers);
	}

	@Override
	public Map<String, String> availableDrivers() {
		return drivers;
	}

	public void setDrivers(Map<String, String> drivers) {
		this.drivers = drivers;
	}

}
