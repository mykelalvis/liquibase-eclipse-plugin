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

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.Wizard;

import com.arjuna.ats.jta.UserTransaction;

/**
 * @author nick
 */
public class RollbackChangeSetWizard extends Wizard {

	/**
	 * The list of files to attempt to roll back.
	 */
	private List<ChangeSetTreeItem> items;

	/**
	 * Page showing currently installed scripts.
	 */
	private RollbackSummaryPage rollbackPage;

	/**
	 * @param changeSets
	 *            A list of selected script files.
	 */
	public RollbackChangeSetWizard(final List<ChangeSetTreeItem> changeSets) {
		items = changeSets;
	}

	@Override
	public final void addPages() {
		rollbackPage = new RollbackSummaryPage(items);
		addPage(rollbackPage);
	}

	@Override
	public final boolean performFinish() {
		boolean ok = true;
		final Job job = new Job("Rollback Liquibase Script") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("rollback", items.size());
				for (final ChangeSetTreeItem item : items) {
					if (!monitor.isCanceled()) {
						runScript(item);
					}
					monitor.worked(1);
				}
				monitor.done();
				return Status.OK_STATUS;
			}

			private void runScript(final ChangeSetTreeItem item) {
				final LiquibaseResult result = new LiquibaseResult();
				result.setStatus(LiquibaseResultStatus.RUNNING);
				result.setTimestamp(System.currentTimeMillis());
				result.setScript(item.getChangeSet().getChangeLog());
				Activator.getDefault().getResults().add(result);
				final IFile changeLogFile = item.getChangeLogFile();
				final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
						changeLogFile.getParent().getLocation().toString());
				IConnectionProfile profile = item.getProfile();
				final Connection connection = ConnectionUtil
						.getConnection(profile);
				if (connection != null) {
					try {
						final javax.transaction.UserTransaction ut = UserTransaction
								.userTransaction();
						ut.begin();
						try {
							final DatabaseConnection database = new JdbcConnection(
									connection);
							final Liquibase lb = new Liquibase(changeLogFile
									.getProjectRelativePath().toString(),
									resourceAccessor, database);
							lb.rollback((String) null, null);
							ut.commit();
							result.setStatus(LiquibaseResultStatus.SUCCESS);
						} catch (final LiquibaseException e) {
							e.printStackTrace();
							ut.rollback();
							result.setStatus(LiquibaseResultStatus.FAILURE);
						}
					} catch (final Exception e) {
						e.printStackTrace();
						result.setStatus(LiquibaseResultStatus.FAILURE);
					} finally {
						try {
							connection.close();
						} catch (final SQLException e) {
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("Failed to get connection.");
					result.setStatus(LiquibaseResultStatus.FAILURE);
				}
			}

		};
		job.schedule();
		return ok;
	}
}
