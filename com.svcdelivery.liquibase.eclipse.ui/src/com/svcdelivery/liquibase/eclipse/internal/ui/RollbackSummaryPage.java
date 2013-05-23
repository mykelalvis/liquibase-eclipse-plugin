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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.svcdelivery.liquibase.eclipse.api.ChangeSetItem;

/**
 * @author nick
 */
public class RollbackSummaryPage extends WizardPage implements CompleteListener {

	/**
	 * Change set items.
	 */
	private ChangeSetTreeItem item;

	/**
	 * Change Set table.
	 */
	private ChangeSetTable cst;

	/**
	 * The files to roll back.
	 */
	private IFile file;

	/**
	 * Constructor to allow change set item to be set later.
	 */
	public RollbackSummaryPage() {
		super("Rollback Summary");
		setTitle("Rollback Summary");
		setMessage("Click Finish to apply the rollbacks.");
		setPageComplete(false);
	}

	/**
	 * Constructor for a known change set item.
	 * 
	 * @param changeSetItem
	 *            Change set items.
	 */
	protected RollbackSummaryPage(final ChangeSetTreeItem changeSetItem) {
		this();
		item = changeSetItem;
	}

	/**
	 * Constructor for a known change set item.
	 * 
	 * @param file
	 *            The file to roll back.
	 */
	protected RollbackSummaryPage(final IFile rollbackFile) {
		this();
		file = rollbackFile;
	}

	/**
	 * @param parent
	 *            The parent composite.
	 * @see org.eclipse.jface.dialogs.IDialogPage
	 *      #createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public final void createControl(final Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout());
		cst = new ChangeSetTable(root, SWT.NONE);
		cst.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cst.addCompletelistener(this);
		if (item != null) {
			cst.setInput(item);
		}
		Button projects = new Button(root, SWT.PUSH);
		projects.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		projects.setText("Add Projects");
		projects.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				ProjectDialog dialog = new ProjectDialog(getShell());
				dialog.open();
			}
		});
		setControl(root);
	}

	@Override
	public final void complete(final boolean isComplete,
			final Object changeSetItem) {
		if (changeSetItem instanceof IConnectionProfile && file != null) {
			final IConnectionProfile profile = (IConnectionProfile) changeSetItem;
			LiquibaseDataSourceScriptLoader loader = new LiquibaseDataSourceScriptLoader() {

				@Override
				public void complete(List<ChangeSetItem> ranChangeSets) {
					if (ranChangeSets.size() != 0) {
						ChangeSetItem set = null;
						String filename = file.getName();
						for (ChangeSetItem next : ranChangeSets) {
							if (filename.equals(next.getChangeLog())) {
								set = next;
								break;
							}
						}
						if (set != null) {
							item = new ChangeSetTreeItem();
							item.setProfile(profile);
							item.setChangeSet(set);
							if (cst != null) {
								cst.setInput(item);
							}
						}
					} else {
						// Show error, no change sets found.
					}
				}
			};
			loader.loadScripts(profile);
		} else if (changeSetItem instanceof ChangeSetTreeItem) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					setPageComplete(isComplete);
				}
			});
		}
	}

	/**
	 * @return The list of items to roll back.
	 */
	public final List<ChangeSetTreeItem> getRollbackList() {
		return cst.getRollbackList();
	}

}
