package com.svcdelivery.liquibase.eclipse.ui.preferences;

import java.util.Arrays;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.ServiceReference;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;
import com.svcdelivery.liquibase.eclipse.internal.ui.Activator;
import com.svcdelivery.liquibase.eclipse.internal.ui.CollectionContentProvider;

/**
 * @author nick
 */
public class LiquibasePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private ServiceReference<LiquibaseService> activeService;
	private TableViewer versionViewer;

	public LiquibasePreferencePage() {
	}

	public LiquibasePreferencePage(String title) {
		super(title);
	}

	public LiquibasePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		root.setLayoutData(data);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		root.setLayout(layout);

		Text versionLabel = new Text(root, SWT.NONE);
		versionLabel.setText("Liqubase Version:");
		versionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));

		versionViewer = new TableViewer(root);
		Table versionTable = versionViewer.getTable();
		versionTable
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		IContentProvider versionContentProvider = new CollectionContentProvider();
		ITableLabelProvider versionLabelProvider = new LiquibaseServicesLabelProvider();
		versionViewer.setContentProvider(versionContentProvider);
		versionViewer.setLabelProvider(versionLabelProvider);
		ServiceReference<LiquibaseService>[] liquibaseServices = Activator
				.getDefault().getLiquibaseServices();
		if (liquibaseServices != null) {
			versionViewer.setInput(Arrays.asList(liquibaseServices));
		}

		activeService = Activator.getDefault()
				.getActiveLiquibaseServiceReference();

		if (activeService != null) {
			ISelection selection = new StructuredSelection(activeService);
			versionViewer.setSelection(selection, true);
		}

		return root;
	}

	@Override
	public boolean performOk() {
		StructuredSelection selection = (StructuredSelection) versionViewer
				.getSelection();
		if (selection.size() == 1) {
			ServiceReference<LiquibaseService> svc = (ServiceReference<LiquibaseService>) selection
					.getFirstElement();
			Activator.getDefault().setDefaultLiquibaseService(svc);
		}
		return super.performOk();
	}

}
