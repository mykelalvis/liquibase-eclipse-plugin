/**
 * Copyright 2013 Nick Wilson
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
package com.svcdelivery.liquibase.eclipse.ui.preferences;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseProvider;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;
import com.svcdelivery.liquibase.eclipse.internal.collections.NotifyingArrayList;
import com.svcdelivery.liquibase.eclipse.internal.ui.Activator;
import com.svcdelivery.liquibase.eclipse.internal.ui.CollectionContentProvider;
import com.svcdelivery.liquibase.eclipse.internal.ui.version.AddVersionWizard;

/**
 * @author nick
 */
public class LiquibasePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private ServiceReference<LiquibaseService> activeService;
	private TableViewer versionViewer;
	private TableViewer serviceViewer;

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
	protected final Control createContents(final Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		root.setLayoutData(data);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		root.setLayout(layout);

		Label versionLabel = new Label(root, SWT.NONE);
		versionLabel.setText("Liqubase Version:");
		versionLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		versionViewer = new TableViewer(root, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.FLAT);
		Table versionTable = versionViewer.getTable();
		versionTable
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableViewerColumn providerColumn = new TableViewerColumn(versionViewer,
				SWT.NONE);
		providerColumn.getColumn().setWidth(220);
		providerColumn.getColumn().setText("Provider");
		TableViewerColumn versionColumn = new TableViewerColumn(versionViewer,
				SWT.NONE);
		versionColumn.getColumn().setWidth(100);
		versionColumn.getColumn().setText("Version");
		versionTable.setHeaderVisible(true);
		IContentProvider versionContentProvider = new CollectionContentProvider<ServiceReference<LiquibaseService>>();
		ITableLabelProvider versionLabelProvider = new LiquibaseServicesLabelProvider();
		versionViewer.setContentProvider(versionContentProvider);
		versionViewer.setLabelProvider(versionLabelProvider);
		List<ServiceReference<LiquibaseService>> liquibaseServices = Activator
				.getDefault().getLiquibaseServices();
		if (liquibaseServices != null) {
			versionViewer.setInput(liquibaseServices);
		}

		Label serviceLabel = new Label(root, SWT.NONE);
		serviceLabel.setText("Additional Services:");
		serviceLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		serviceViewer = new TableViewer(root, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.FLAT);
		Table serviceTable = serviceViewer.getTable();
		serviceTable
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		serviceViewer.setContentProvider(new CollectionContentProvider<URL>());
		serviceViewer.setLabelProvider(new URLLabelProvider());

		Button add = new Button(root, SWT.PUSH);
		add.setText("Add");
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Show Add Version wizard
				AddVersionWizard addVersion = new AddVersionWizard();
				WizardDialog dialog = new WizardDialog(getShell(), addVersion);
				dialog.setPageSize(400, 400);
				dialog.open();
			}
		});
		Button remove = new Button(root, SWT.PUSH);
		remove.setText("Remove");
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Version selectedVersion = getSelectedVersion();
				if (selectedVersion != null) {
					Activator activator = Activator.getDefault();
					activator.removeDescriptor(selectedVersion);
				}
			}
		});
		versionViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						Version version = getSelectedVersion();
						Activator activator = Activator.getDefault();
						ServiceReference<LiquibaseProvider> provider = activator
								.getLiquibaseProvider(version);
						if (provider != null) {
							// TODO get library URL list.
							BundleContext ctx = Activator.getDefault()
									.getBundle().getBundleContext();
							LiquibaseProvider service = ctx
									.getService(provider);
							URL[] libraries = service.getLibraries(version);
							if (libraries == null) {
								libraries = new URL[0];
							}
							NotifyingArrayList<URL> list = new NotifyingArrayList<URL>(
									libraries);
							serviceViewer.setInput(list);
							ctx.ungetService(provider);
						} else {
							serviceViewer.setInput(null);
						}
					}
				});

		activeService = Activator.getDefault()
				.getActiveLiquibaseServiceReference();

		if (activeService != null) {
			ISelection selection = new StructuredSelection(activeService);
			versionViewer.setSelection(selection, true);
		}
		applyDialogFont(root);

		return root;
	}

	private Version getSelectedVersion() {
		Version version = null;
		ServiceReference<LiquibaseService> ref = getSelectedLibrary();
		if (ref != null) {
			version = Version.parseVersion((String) ref.getProperty("version"));
		}
		return version;
	}

	private ServiceReference<LiquibaseService> getSelectedLibrary() {
		ServiceReference<LiquibaseService> ref = null;
		ISelection selection = versionViewer.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			if (ss.size() == 1) {
				ref = (ServiceReference<LiquibaseService>) ss.getFirstElement();
			}
		}
		return ref;
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

	@Override
	protected void performDefaults() {
		ISelection selection = new StructuredSelection(activeService);
		versionViewer.setSelection(selection);
	}

}
