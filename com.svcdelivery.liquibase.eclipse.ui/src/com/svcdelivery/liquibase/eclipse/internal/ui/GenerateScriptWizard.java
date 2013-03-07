package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.Diff;
import liquibase.diff.DiffResult;
import liquibase.diff.DiffStatusListener;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;

import com.arjuna.ats.jta.UserTransaction;

public class GenerateScriptWizard extends Wizard {

	private IResource selected;
	/**
	 * The data source selection page.
	 */
	private DataSourcePage dataSourcePage;

	private SchemaPickerPage schemaPickerPage;

	private TargetFilePage targetFilePage;

	public GenerateScriptWizard(IResource selected) {
		this.selected = selected;
	}

	@Override
	public final void addPages() {
		dataSourcePage = new DataSourcePage(SWT.SINGLE);
		schemaPickerPage = new SchemaPickerPage();
		targetFilePage = new TargetFilePage();
		dataSourcePage.addPageCompleteListener(schemaPickerPage);
		addPage(dataSourcePage);
		addPage(schemaPickerPage);
		addPage(targetFilePage);
	}

	@Override
	public boolean performFinish() {
		Job job = new Job("Generate Script") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IConnectionProfile profile = dataSourcePage.getProfile();
				final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
						targetFilePage.getTargetContainer().getLocation()
								.toString());
				final Connection connection = ConnectionUtil
						.getConnection(profile);
				IFile target = targetFilePage.getTargetContainer().getFile(
						new Path(targetFilePage.getFilename()));
				String changeLogFile = target.getName();
				if (connection != null) {
					try {
						final javax.transaction.UserTransaction ut = UserTransaction
								.userTransaction();
						ut.begin();
						try {
							final DatabaseConnection database = new JdbcConnection(
									connection);
							final Liquibase lb = new Liquibase(changeLogFile,
									resourceAccessor, database);
							Database targetDb = lb.getDatabase();
							Diff diff = lb.diff(null, targetDb);
							diff.addStatusListener(new DiffStatusListener() {

								@Override
								public void statusUpdate(String message) {
									// TODO Auto-generated method stub
								}
							});
							DiffResult diffResult = diff.compare();
							PipedOutputStream pos = new PipedOutputStream();
							InputStream source = new PipedInputStream(pos);
							target.create(source , true, null);
							PrintStream out = new PrintStream(pos);
							diffResult.printChangeLog(out , targetDb);
							out.flush();
							out.close();
							source.close();
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
					DatabaseUpdateEvent event = new DatabaseUpdateEvent(profile);
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
