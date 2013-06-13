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

import java.util.Iterator;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * @author nick
 */
public class DataSourceViewPart extends ViewPart implements
		DatabaseUpdateListener {
	private TreeViewer dataSources;

	@Override
	public final void createPartControl(final Composite parent) {
		dataSources = new TreeViewer(parent, SWT.VIRTUAL | SWT.FULL_SELECTION);
		final Tree dataSourcesTree = dataSources.getTree();
		TreeViewerColumn log = new TreeViewerColumn(dataSources, SWT.NONE);
		TreeViewerColumn id = new TreeViewerColumn(dataSources, SWT.NONE);
		TreeViewerColumn tag = new TreeViewerColumn(dataSources, SWT.NONE);
		TreeViewerColumn date = new TreeViewerColumn(dataSources, SWT.NONE);
		TreeViewerColumn type = new TreeViewerColumn(dataSources, SWT.NONE);
		TreeColumn logColumn = log.getColumn();
		TreeColumn idColumn = id.getColumn();
		TreeColumn tagColumn = tag.getColumn();
		TreeColumn dateColumn = date.getColumn();
		TreeColumn typeColumn = type.getColumn();
		logColumn.setText("");
		idColumn.setText("ID");
		tagColumn.setText("Tag");
		dateColumn.setText("Date Ran");
		typeColumn.setText("Type");
		logColumn.setWidth(300);
		idColumn.setWidth(150);
		tagColumn.setWidth(150);
		dateColumn.setWidth(150);
		typeColumn.setWidth(100);

		dataSourcesTree.setHeaderVisible(true);
		dataSourcesTree.setLinesVisible(true);
		dataSources.setUseHashlookup(true);
		dataSources.setContentProvider(new DataSourceContentProvider());
		dataSources.setLabelProvider(new DataSourceLabelProvider());
		dataSources.setInput(ProfileManager.getInstance());

		getSite().setSelectionProvider(dataSources);

		MenuManager mgr = new MenuManager();
		mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		mgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				manager.removeAll();
				ISelection selection = dataSources.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection ss = (StructuredSelection) selection;
					boolean rollback = true;
					boolean refresh = true;
					Iterator<?> i = ss.iterator();
					while (i.hasNext()) {
						Object next = i.next();
						if (!(next instanceof ChangeSetTreeItem)) {
							rollback = false;
							break;
						}
						if (!(next instanceof IConnectionProfile)) {
							refresh = false;
							break;
						}
					}
					if (rollback) {
						Shell shell = dataSourcesTree.getShell();
						IAction action = new RollbackCommandHandler(shell,
								selection);
						manager.add(action);
					}
					if (refresh) {
						IAction action = new DataSourceRefreshCommandHandler(
								selection);
						manager.add(action);
					}
				}
			}
		});
		getSite().registerContextMenu(mgr, dataSources);
		Menu menu = mgr.createContextMenu(dataSourcesTree);
		dataSourcesTree.setMenu(menu);

		Activator.getDefault().addDatabaseUpdateListener(this);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public final void dispose() {
		Activator.getDefault().removeDatabaseUpdateListener(this);
		super.dispose();
	}

	@Override
	public final void databaseUpdated(final DatabaseUpdateEvent event) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				dataSources.refresh(event.getElement());
			}
		});
	}

}
