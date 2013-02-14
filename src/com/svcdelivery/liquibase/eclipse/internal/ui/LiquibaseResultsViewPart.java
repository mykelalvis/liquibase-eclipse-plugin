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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * @author nick
 */
public class LiquibaseResultsViewPart extends ViewPart {

	/**
	 * The results table.
	 */
	private TableViewer resultsTable;

	@Override
	public final void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout());
		resultsTable = new TableViewer(parent);
		final Table table = resultsTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final TableColumn dateTimeColumn = new TableColumn(table, SWT.NONE);
		dateTimeColumn.setText("Timestamp");
		dateTimeColumn.setWidth(150);
		final TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText("Script");
		nameColumn.setWidth(250);
		resultsTable.setContentProvider(new LiquibaseResultsContentProvider());
		resultsTable.setLabelProvider(new LiquibaseResultsLabelProvider());
		final LiquibaseResults results = Activator.getDefault().getResults();
		results.addLiquibaseResultChangeListener(new LiquibaseResultChangeListener() {

			@Override
			public void resultChanged(final LiquibaseChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						final LiquibaseResult result = event.getResult();
						if (LiquibaseChangeEventType.CHANGE.equals(event
								.getType())) {
							resultsTable.update(result, null);
						} else {
							resultsTable.refresh();
						}
					}
				});
			}
		});
		resultsTable.setInput(results);
	}

	@Override
	public void setFocus() {
	}

}
