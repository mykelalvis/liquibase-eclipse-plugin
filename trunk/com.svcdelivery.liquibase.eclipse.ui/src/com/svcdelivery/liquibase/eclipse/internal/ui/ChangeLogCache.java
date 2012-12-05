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

import java.util.List;

import liquibase.changelog.DatabaseChangeLog;

/**
 * Holds the change log scripts detected by the builder.
 * 
 * TODO: Hold change logs as a tree to show imports.
 * 
 * @author nick
 */
public class ChangeLogCache {
	/**
	 * List of cached change logs.
	 */
	private List<DatabaseChangeLog> logs;

	/**
	 * @return The cached change logs.
	 */
	public final List<DatabaseChangeLog> getChangeLogs() {
		return logs;
	}

	/**
	 * @param changeLogs
	 *            The change logs to cache.
	 */
	final void setChangeLogs(final List<DatabaseChangeLog> changeLogs) {
		logs = changeLogs;
	}

}
