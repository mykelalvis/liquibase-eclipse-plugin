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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author nick
 */
public class ChangeSetTable extends Composite {

	/**
	 * The table viewer.
	 */
	private TableViewer tv;

	/**
	 * A list of listeners for completion events.
	 */
	private List<CompleteListener> listeners;

	/**
	 * Tree items.
	 */
	private List<ChangeSetTreeItem> items;

	/**
	 * True if all entries complete.
	 */
	private boolean complete;

	/**
	 * @param parent
	 *            The parent composite.
	 * @param style
	 *            The composite style.
	 */
	public ChangeSetTable(final Composite parent, final int style) {
		super(parent, style);
		setLayout(new FillLayout());
		listeners = new ArrayList<CompleteListener>();
		tv = new TableViewer(this);
		final TableViewerColumn changeSetCV = new TableViewerColumn(tv,
				SWT.NONE);
		final TableColumn changeSetC = changeSetCV.getColumn();
		changeSetC.setText("Change Set");
		changeSetC.setWidth(250);
		final TableViewerColumn changeDateCV = new TableViewerColumn(tv,
				SWT.NONE);
		final TableColumn changeDateC = changeDateCV.getColumn();
		changeDateC.setText("Date");
		changeDateC.setWidth(150);
		final TableViewerColumn changeFileCV = new TableViewerColumn(tv,
				SWT.NONE);
		final TableColumn changeFileC = changeFileCV.getColumn();
		changeFileC.setText("File");
		changeFileC.setWidth(250);

		final TableViewerColumn statusCV = new TableViewerColumn(tv, SWT.NONE);
		final TableColumn statusC = statusCV.getColumn();
		statusC.setText("Status");
		statusC.setWidth(100);
		tv.setContentProvider(new CollectionContentProvider());
		tv.setLabelProvider(new ChangeSetLabelProvider());
	}

	/**
	 * @param input
	 *            The input object.
	 */
	public final void setInput(final List<ChangeSetTreeItem> input) {
		tv.setInput(input);
		items = input;
		checkComplete();
	}

	/**
	 * Checks if items are complete.
	 */
	private void checkComplete() {
		boolean ok = true;
		for (ChangeSetTreeItem item : items) {
			if (item.getChangeLogFile() == null) {
				ok = false;
			}
		}
		complete = ok;
		notifyListeners();
	}

	/**
	 * Notify listeners of complete state.
	 */
	private void notifyListeners() {
		for (CompleteListener listener : listeners) {
			notifyListener(listener);
		}
	}

	/**
	 * @param listener
	 *            The listener to notify.
	 */
	private void notifyListener(final CompleteListener listener) {
		listener.complete(complete);
	}

	/**
	 * @param listener
	 *            the listener.
	 */
	public final void addCompletelistener(final CompleteListener listener) {
		listeners.add(listener);
		notifyListener(listener);
	}
}
