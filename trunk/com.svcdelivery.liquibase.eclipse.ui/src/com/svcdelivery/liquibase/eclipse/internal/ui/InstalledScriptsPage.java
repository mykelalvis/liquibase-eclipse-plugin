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
import java.util.ArrayList;
import java.util.Iterator;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This page shows a list of scripts that are currently installed and allows
 * selection of one or more.
 * 
 * @author nickw
 * 
 */
public class InstalledScriptsPage extends WizardPage {

	/**
	 * Job display text.
	 */
	private static final String JOB_NAME = "Loading installed scripts ...";
	/**
	 * The data source page.
	 */
	private final DataSourcePage dsPage;
	/**
	 * The root composite.
	 */
	private Composite root;
	/**
	 * The "please wait" label.
	 */
	private Label wait;
	/**
	 * Change set picker table.
	 */
	private TableViewer scriptPicker;
	/**
	 * Available change sets.
	 */
	private List<RanChangeSet> ranChangeSets;
	/**
	 * Selected change sets.
	 */
	private List<RanChangeSet> selectedChangeSets;

	/**
	 * @param dataSourcePage
	 *            The data source page.
	 */
	protected InstalledScriptsPage(final DataSourcePage dataSourcePage) {
		super("Installed Scripts");
		setTitle("Installed Scripts");
		setMessage("Select the Scripts.");
		dsPage = dataSourcePage;
		setPageComplete(false);
		dsPage.addPageCompleteListener(new CompleteListener() {

			@Override
			public void complete(final boolean isComplete) {
				if (isComplete) {
					loadScripts();
				}
			}
		});
	}

	@Override
	public final void createControl(final Composite parent) {
		root = new Composite(parent, SWT.NONE);
		root.setLayout(new FillLayout());

		wait = new Label(root, SWT.NONE);
		wait.setText(JOB_NAME);
		setControl(root);
	}

	/**
	 * Load the available change sets then update the UI.
	 */
	private void loadScripts() {
		final Job loadScripts = new Job(JOB_NAME) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				IStatus status = Status.CANCEL_STATUS;
				final Connection connection = ConnectionUtil
						.getConnection(dsPage.getProfile());
				if (connection != null) {
					final DatabaseConnection dbConnection = new JdbcConnection(
							connection);
					try {
						final Database database = DatabaseFactory
								.getInstance()
								.findCorrectDatabaseImplementation(dbConnection);
						ranChangeSets = database.getRanChangeSetList();
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								showTable();
							}

						});
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
				return status;
			}
		};
		loadScripts.schedule();
	}

	/**
	 * Show the change set table.
	 */
	private void showTable() {
		wait.dispose();
		scriptPicker = new TableViewer(root);
		final TableViewerColumn changeSetCV = new TableViewerColumn(
				scriptPicker, SWT.NONE);
		final TableColumn changeSetC = changeSetCV.getColumn();
		changeSetC.setText("Change Set");
		changeSetC.setWidth(250);
		final TableViewerColumn changeDateCV = new TableViewerColumn(
				scriptPicker, SWT.NONE);
		final TableColumn changeDateC = changeDateCV.getColumn();
		changeDateC.setText("Date");
		changeDateC.setWidth(150);
		scriptPicker.setContentProvider(new CollectionContentProvider());
		scriptPicker.setLabelProvider(new ChangeSetLabelProvider());
		scriptPicker.setInput(ranChangeSets);
		scriptPicker
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(
							final SelectionChangedEvent event) {
						final ISelection selection = event.getSelection();
						if (selection instanceof StructuredSelection) {
							final StructuredSelection ss = (StructuredSelection) selection;
							selectedChangeSets = new ArrayList<RanChangeSet>();
							final Iterator<?> i = ss.iterator();
							while (i.hasNext()) {
								final Object next = i.next();
								if (next instanceof RanChangeSet) {
									selectedChangeSets.add((RanChangeSet) next);
								}
							}
						}
						updatePageComplete();
					}
				});
		root.layout();
	}

	/**
	 * Update the page complete status.
	 */
	private void updatePageComplete() {
		final boolean ok = selectedChangeSets != null
				&& selectedChangeSets.size() != 0;
		setPageComplete(ok);
	}

	/**
	 * @return The selected change sets.
	 */
	public final List<RanChangeSet> getChangeSets() {
		return selectedChangeSets;
	}
}
