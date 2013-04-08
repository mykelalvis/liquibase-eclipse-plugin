/**
 * Copyright 2012 Nick Wilson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.sql.Connection;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;

/**
 * @author nick
 */
public final class ConnectionUtil {

	/**
	 * Private Constructor.
	 */
	private ConnectionUtil() {
	}

	/**
	 * @param profile
	 *            The connection profile.
	 * @return The connction.
	 */
	public static Connection getConnection(final IConnectionProfile profile) {
		Connection connection = null;
		if (profile.arePropertiesComplete()) {
			final IConnection conn = profile
					.createConnection("java.sql.Connection");
			if (conn != null) {
				final Object raw = conn.getRawConnection();
				if (raw instanceof Connection) {
					connection = (Connection) raw;
				}
			}
		}
		return connection;
	}

}
