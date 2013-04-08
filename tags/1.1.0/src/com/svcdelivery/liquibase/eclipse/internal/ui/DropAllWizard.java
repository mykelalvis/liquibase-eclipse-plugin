package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.sql.Connection;
import java.sql.SQLException;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;

import com.arjuna.ats.jta.UserTransaction;

public class DropAllWizard extends Wizard {

	private IConnectionProfile selected;
	/**
	 * The data source selection page.
	 */
	private DataSourcePage dataSourcePage;

	private SchemaPickerPage schemaPickerPage;

	public DropAllWizard(IConnectionProfile selected) {
		this.selected = selected;
	}

	@Override
	public final void addPages() {
		schemaPickerPage = new SchemaPickerPage();
		if (selected == null) {
			dataSourcePage = new DataSourcePage(SWT.MULTI);
			addPage(dataSourcePage);
			dataSourcePage.addPageCompleteListener(schemaPickerPage);
		} else {
			schemaPickerPage.complete(true, selected);
		}
		addPage(schemaPickerPage);
	}

	@Override
	public boolean performFinish() {
		Job job = new Job("Drop All") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (selected == null) {
					selected = dataSourcePage.getProfile();
				}
				final Connection connection = ConnectionUtil
						.getConnection(selected);
				if (connection != null) {
					try {
						final javax.transaction.UserTransaction ut = UserTransaction
								.userTransaction();
						ut.begin();
						try {
							final DatabaseConnection database = new JdbcConnection(
									connection);
							Database targetDb = DatabaseFactory
									.getInstance()
									.findCorrectDatabaseImplementation(database);
							String schema = schemaPickerPage.getSchema();
							targetDb.dropDatabaseObjects(schema);
							ut.commit();
						} catch (final LiquibaseException e) {
							e.printStackTrace();
							ut.rollback();
						}
					} catch (final Exception e) {
						e.printStackTrace();
					} finally {
						try {
							connection.close();
						} catch (final SQLException e) {
							e.printStackTrace();
						}
					}
					DatabaseUpdateEvent event = new DatabaseUpdateEvent(
							selected);
					Activator.getDefault().notifyDatabaseUpdateListeners(event);
				} else {
					System.out.println("Failed to get connection.");
				}

				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return true;
	}
}
