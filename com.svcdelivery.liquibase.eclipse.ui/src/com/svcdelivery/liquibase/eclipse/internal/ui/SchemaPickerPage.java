package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SchemaPickerPage extends WizardPage implements CompleteListener {

	/**
	 * The job name.
	 */
	private static final String JOB_NAME = "Loading schema list ...";

	private ListViewer schemaList;

	private String selectedSchema;

	/**
	 * Constructor to allow change set item to be set later.
	 */
	public SchemaPickerPage() {
		super("Select Schema");
		setTitle("Select Schema");
		setMessage("Click Finish to generate a changelog script.");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout());
		schemaList = new ListViewer(parent);
		schemaList.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		schemaList.setContentProvider(new CollectionContentProvider());
		schemaList.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedSchema = null;
				ISelection selection = schemaList.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection structured = (StructuredSelection) selection;
					if (structured.size() == 1) {
						selectedSchema = (String) structured.getFirstElement();
					}
				}
				setPageComplete(selectedSchema != null);
			}
		});
		setControl(root);
	}

	@Override
	public void complete(boolean isComplete, Object item) {
		if (item instanceof IConnectionProfile) {
			final IConnectionProfile profile = (IConnectionProfile) item;
			final Job loadSchemas = new Job(JOB_NAME) {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					IStatus status = Status.CANCEL_STATUS;
					Connection connection = null;
					ResultSet schemaSet = null;
					try {
						connection = ConnectionUtil.getConnection(profile);
						DatabaseMetaData md = connection.getMetaData();
						schemaSet = md.getSchemas();
						final List<String> schemas = new ArrayList<String>();
						while (schemaSet.next()) {
							String schema = schemaSet.getString(1);
							schemas.add(schema);
						}
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								schemaList.setInput(schemas);
							}
						});
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (schemaSet != null) {
							try {
								schemaSet.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
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
			loadSchemas.schedule();
		}
	}

}
