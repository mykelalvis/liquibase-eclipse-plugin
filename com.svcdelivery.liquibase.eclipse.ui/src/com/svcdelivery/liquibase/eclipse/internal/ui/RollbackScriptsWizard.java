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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;

import com.arjuna.ats.jta.UserTransaction;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

/**
 * @author nick
 */
public class RollbackScriptsWizard extends Wizard {

	/**
	 * The file to attempt to roll back.
	 */
	private final IFile file;

	/**
	 * Data source selection page.
	 */
	private DataSourcePage dataSourcePage;

	/**
	 * Page showing currently installed scripts.
	 */
	private RollbackSummaryPage rollbackPage;

	/**
	 * @param scriptFile
	 *            The selected script file.
	 */
	public RollbackScriptsWizard(final IFile scriptFile) {
		file = scriptFile;
	}

	@Override
	public final void addPages() {
		dataSourcePage = new DataSourcePage(SWT.NONE);
		rollbackPage = new RollbackSummaryPage(file);
		dataSourcePage.addPageCompleteListener(rollbackPage);
		addPage(dataSourcePage);
		addPage(rollbackPage);
	}

	@Override
	public final boolean performFinish() {
		boolean ok = true;
		final Job job = new Job("Rollback Liquibase Script") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				List<ChangeSetTreeItem> rollbackList = rollbackPage
						.getRollbackList();
				Collections.reverse(rollbackList);
				monitor.beginTask("rollback", rollbackList.size());
				if (!monitor.isCanceled()) {
					String filename = null;
					IFile file = null;
					IFile toRollBack = null;
					int count = 0;
					for (ChangeSetTreeItem rollback : rollbackList) {
						file = rollback.getChangeLogFile();
						String itemName = file.getName();
						if (filename == null) {
							toRollBack = file;
							filename = itemName;
							count++;
						} else if (!filename.equals(itemName)) {
							runScript(toRollBack, count);
							toRollBack = file;
							count = 1;
							filename = null;
						} else {
							count++;
						}
						monitor.worked(1);
					}
					runScript(toRollBack, count);
				}
				monitor.done();
				return Status.OK_STATUS;
			}

			private void runScript(final IFile changeLogFile, final int count) {
				final LiquibaseResult result = new LiquibaseResult();
				result.setStatus(LiquibaseResultStatus.RUNNING);
				result.setTimestamp(System.currentTimeMillis());
				result.setScript(changeLogFile.getName());
				Activator.getDefault().getResults().add(result);
				IConnectionProfile profile = dataSourcePage.getProfile();
				final Connection connection = ConnectionUtil
						.getConnection(profile);
				if (connection != null) {
					try {
						final javax.transaction.UserTransaction ut = UserTransaction
								.userTransaction();
						ut.begin();
						try {
							LiquibaseService ls = Activator.getDefault()
									.getActiveLiquibaseService();
							ls.rollback(changeLogFile.getLocation().toFile(),
									count, connection);
							ut.commit();
							result.setStatus(LiquibaseResultStatus.SUCCESS);
						} catch (final LiquibaseApiException e) {
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
					// Notify change to database.
					DatabaseUpdateEvent event = new DatabaseUpdateEvent(
							dataSourcePage.getProfile());
					Activator.getDefault().notifyDatabaseUpdateListeners(event);
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
