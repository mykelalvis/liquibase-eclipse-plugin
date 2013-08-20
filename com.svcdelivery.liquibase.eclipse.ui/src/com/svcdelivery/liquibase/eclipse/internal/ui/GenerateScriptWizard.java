package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.resources.IContainer;
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
import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

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
			protected IStatus run(final IProgressMonitor monitor) {
				IConnectionProfile profile = dataSourcePage.getProfile();
				final Connection connection = ConnectionUtil
						.getConnection(profile);
				IContainer parent = targetFilePage.getTargetContainer();
				final IFile target = parent.getFile(new Path(targetFilePage
						.getFilename()));
				if (connection != null) {
					try {
						final javax.transaction.UserTransaction ut = UserTransaction
								.userTransaction();
						ut.begin();
						try {
							LiquibaseService ls = Activator.getDefault()
									.getActiveLiquibaseService();
							ls.diff(connection, schemaPickerPage.getSchema(),
									target.getLocation().toFile());
							ut.commit();
							target.refreshLocal(IResource.DEPTH_ZERO, null);
						} catch (final LiquibaseApiException e) {
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
