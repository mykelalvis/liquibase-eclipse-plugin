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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * View to show all of the available scripts in the workspace.
 * 
 * @author nick
 */
public class ScriptsViewPart extends ViewPart implements ChangeLogListener {

	private TreeViewer scripts;

	/**
	 * @param parent
	 *            The parent composite.
	 * @see org.eclipse.ui.part.WorkbenchPart
	 *      #createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public final void createPartControl(final Composite parent) {
		scripts = new TreeViewer(parent);
		Tree scriptsTree = scripts.getTree();
		TreeColumn nameColumn = new TreeColumn(scriptsTree, SWT.NONE);
		nameColumn.setText("Name");
		nameColumn.setWidth(300);
		TreeColumn projectColumn = new TreeColumn(scriptsTree, SWT.NONE);
		projectColumn.setText("Project");
		projectColumn.setWidth(150);
		TreeColumn pathColumn = new TreeColumn(scriptsTree, SWT.NONE);
		pathColumn.setText("Path");
		pathColumn.setWidth(350);
		ChangeLogCache changeLogCache = Activator.getDefault()
				.getChangeLogCache();
		scripts.setContentProvider(new ScriptsTreeContentProvider());
		scripts.setLabelProvider(new ScriptsTreeLabelProvider());
		scripts.setInput(changeLogCache);
		changeLogCache.addChangeLogListener(this);
	}

	@Override
	public void dispose() {
		ChangeLogCache changeLogCache = Activator.getDefault()
				.getChangeLogCache();
		changeLogCache.removeChangeLogListener(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void changeLogUpdated(final IFile file) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				scripts.refresh();
			}
		});
	}

	@Override
	public void changeLogRemoved(final IFile file) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				scripts.remove(file);
			}
		});
	}

}
