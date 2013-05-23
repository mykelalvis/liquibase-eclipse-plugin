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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

import com.svcdelivery.liquibase.eclipse.api.ChangeSetItem;

/**
 * @author nick
 */
public class ChangeSetTable extends Composite implements ChangeLogListener {

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
	 * Selected item.
	 */
	private ChangeSetTreeItem selected;

	/**
	 * True if all entries complete.
	 */
	private boolean complete;

	/**
	 * The change log cache.
	 */
	private ChangeLogCache cache;

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
		cache = Activator.getDefault().getChangeLogCache();
		cache.addChangeLogListener(this);
	}

	@Override
	public void dispose() {
		cache.removeChangeLogListener(this);
		super.dispose();
	}

	/**
	 * @param input
	 *            The input object.
	 */
	public final void setInput(final ChangeSetTreeItem input) {
		selected = input;
		LiquibaseDataSourceScriptLoader loader = new LiquibaseDataSourceScriptLoader() {

			@Override
			public void complete(List<ChangeSetItem> ranChangeSets) {
				ChangeSetItem toRollback = selected.getChangeSet();
				int index = ranChangeSets.indexOf(toRollback);
				if (index != -1) {
					final List<ChangeSetItem> rollbackList = ranChangeSets
							.subList(index, ranChangeSets.size());
					items = new ArrayList<ChangeSetTreeItem>(
							rollbackList.size());
					for (ChangeSetItem set : rollbackList) {
						ChangeSetTreeItem item = new ChangeSetTreeItem();
						item.setChangeSet(set);
						String filename = set.getChangeLog();
						System.out.println("Filename=" + filename);
						System.out.println("cache=" + cache.toString());
						// FIXME convert to cache name?
						IFile file = cache.getFile(filename);
						if (file != null) {
							item.setChangeLogFile(file);
						}
						item.setProfile(selected.getProfile());
						items.add(item);
					}
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							tv.setInput(items);
						}
					});
					checkComplete();
				}
			}

		};
		loader.loadScripts(selected.getProfile());
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
		listener.complete(complete, selected);
	}

	/**
	 * @param listener
	 *            the listener.
	 */
	public final void addCompletelistener(final CompleteListener listener) {
		listeners.add(listener);
		notifyListener(listener);
	}

	public List<ChangeSetTreeItem> getRollbackList() {
		return items;
	}

	@Override
	public void changeLogUpdated(IFile file) {
		final Set<ChangeSetTreeItem> toUpdate = new HashSet<ChangeSetTreeItem>();
		for (ChangeSetTreeItem item : items) {
			ChangeSetItem set = item.getChangeSet();
			if (set != null) {
				String changelog = set.getChangeLog();
				IFile changelogFile = Activator.getDefault()
						.getChangeLogCache().getFile(changelog);
				if (file.equals(changelogFile)) {
					item.setChangeLogFile(file);
					toUpdate.add(item);
				}
			}
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				tv.update(toUpdate.toArray(), null);
			}
		});
		checkComplete();
	}

	@Override
	public void changeLogRemoved(IFile file) {
		final Set<ChangeSetTreeItem> toUpdate = new HashSet<ChangeSetTreeItem>();
		for (ChangeSetTreeItem item : items) {
			if (file.equals(item.getChangeLogFile())) {
				item.setChangeLogFile(null);
				toUpdate.add(item);
			}
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				tv.update(toUpdate.toArray(), null);
			}
		});
		checkComplete();
	}

}
