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
import java.sql.SQLException;
import java.util.List;

import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;

/**
 * Class to load a list of Liquibase scripts from a connection profile in the
 * background.
 * 
 * @author nick
 */
public abstract class LiquibaseDataSourceScriptLoader {

	/**
	 * The job name.
	 */
	private static final String JOB_NAME = "Loading installed scripts ...";

	/**
	 * @param profile
	 *            The connection profile.
	 */
	public final void loadScripts(final IConnectionProfile profile) {
		final Job loadScripts = new Job(JOB_NAME) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				IStatus status = Status.CANCEL_STATUS;
				Connection connection = null;
				try {
					connection = ConnectionUtil.getConnection(profile);
					if (connection != null) {
						final DatabaseConnection dbConnection = new JdbcConnection(
								connection);
						try {
							DatabaseFactory dbf = DatabaseFactory.getInstance();
							Database db = dbf
									.findCorrectDatabaseImplementation(dbConnection);
							List<RanChangeSet> ranChangeSets = db
									.getRanChangeSetList();
							complete(ranChangeSets);
							status = Status.OK_STATUS;
						} catch (final DatabaseException e) {
							e.printStackTrace();
						} finally {
							try {
								dbConnection.close();
							} catch (final DatabaseException e) {
								e.printStackTrace();
							}
						}
					}
				} finally {
					if (connection != null) {
						try {
							connection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				return status;
			}
		};
		loadScripts.schedule();
	}

	/**
	 * Called when the scripts have been loaded.
	 * 
	 * @param ranChangeSets
	 *            The change sets that have been run.
	 */
	public abstract void complete(List<RanChangeSet> ranChangeSets);
}
