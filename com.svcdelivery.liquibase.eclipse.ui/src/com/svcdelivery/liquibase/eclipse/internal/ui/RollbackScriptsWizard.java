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
import org.eclipse.swt.SWT;

import com.arjuna.ats.jta.UserTransaction;

/**
 * @author nick
 */
public class RollbackScriptsWizard extends Wizard {

	/**
	 * The list of files to attempt to roll back.
	 */
	private final List<IFile> files;

	/**
	 * Data source selection page.
	 */
	private DataSourcePage dataSourcePage;

	/**
	 * Page showing currently installed scripts.
	 */
	private RollbackSummaryPage rollbackPage;

	/**
	 * @param scriptFiles
	 *            A list of selected script files.
	 */
	public RollbackScriptsWizard(final List<IFile> scriptFiles) {
		files = scriptFiles;
	}

	@Override
	public final void addPages() {
		dataSourcePage = new DataSourcePage(SWT.NONE);
		rollbackPage = new RollbackSummaryPage();
		dataSourcePage.addPageCompleteListener(rollbackPage);
		addPage(dataSourcePage);
		addPage(rollbackPage);
	}

	@Override
	public final boolean performFinish() {
		boolean ok = true;
		final IConnectionProfile profile = dataSourcePage.getProfile();
		if (profile != null) {
			final Job job = new Job("Rollback Liquibase Change Sets") {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					monitor.beginTask("rollback", files.size());
					for (final IFile file : files) {
						if (!monitor.isCanceled()) {
							runScript(file);
						}
						monitor.worked(1);
					}
					monitor.done();
					return Status.OK_STATUS;
				}

				private void runScript(final IFile file) {
					final LiquibaseResult result = new LiquibaseResult();
					result.setStatus(LiquibaseResultStatus.RUNNING);
					result.setTimestamp(System.currentTimeMillis());
					result.setScript(file.getName());
					Activator.getDefault().getResults().add(result);
					final String changeLogFile = file.getLocation().toString();
					final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
							file.getParent().getLocation().toString());
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
								final Liquibase lb = new Liquibase(
										changeLogFile, resourceAccessor,
										database);
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
		} else {
			ok = false;
		}
		return ok;
	}
}
