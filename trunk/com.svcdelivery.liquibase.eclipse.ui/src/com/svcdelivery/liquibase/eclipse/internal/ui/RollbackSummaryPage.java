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

import liquibase.changelog.RanChangeSet;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
	 * Constructor to allow change set item to be set later.
	 */
	protected RollbackSummaryPage() {
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
	 * @param parent
	 *            The parent composite.
	 * @see org.eclipse.jface.dialogs.IDialogPage
	 *      #createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public final void createControl(final Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new FillLayout());
		cst = new ChangeSetTable(root, SWT.NONE);
		cst.addCompletelistener(this);
		if (item != null) {
			cst.setInput(item);
		}
		setControl(root);
	}

	@Override
	public final void complete(final boolean isComplete,
			final Object changeSetItem) {
		if (changeSetItem instanceof IConnectionProfile) {
			final IConnectionProfile profile = (IConnectionProfile) changeSetItem;
			LiquibaseDataSourceScriptLoader loader = new LiquibaseDataSourceScriptLoader() {

				@Override
				public void complete(final List<RanChangeSet> ranChangeSets) {
					if (ranChangeSets.size() != 0) {
						item = new ChangeSetTreeItem();
						item.setProfile(profile);
						item.setChangeSet(ranChangeSets.get(0));
						if (cst != null) {
							cst.setInput(item);
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
