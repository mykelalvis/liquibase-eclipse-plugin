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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Show all workspace projects with the Liquibase ones checked. Allow user to
 * check/uncheck projects and modify project nature/builders accordingly.
 * 
 * @author nick
 * 
 */
public class ProjectDialog extends Dialog {

	private CheckboxTableViewer projectTableViewer;

	private IProject[] projects;

	protected ProjectDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Liquibase Projects");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayout(new GridLayout());
		Label text = new Label(contents, SWT.NONE);
		text.setText("Select active Liquibase projects.");
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Table projectTable = new Table(contents, SWT.CHECK);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 250;
		layoutData.minimumHeight = 300;
		projectTable.setLayoutData(layoutData);
		projectTableViewer = new CheckboxTableViewer(projectTable);
		IContentProvider provider = new ArrayContentProvider();
		IBaseLabelProvider labelProvider = new WorkbenchLabelProvider();
		ICheckStateProvider checkStateProvider = new ProjectStateProvider();
		projectTableViewer.setContentProvider(provider);
		projectTableViewer.setLabelProvider(labelProvider);
		projectTableViewer.setCheckStateProvider(checkStateProvider);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		projects = root.getProjects();
		projectTableViewer.setInput(projects);
		return contents;
	}

	@Override
	protected void okPressed() {
		Object[] checked = projectTableViewer.getCheckedElements();
		List<Object> checkedList = Arrays.asList(checked);
		for (IProject project : projects) {
			try {
				if (checkedList.contains(project)) {
					if (!project.hasNature(LiquibaseNature.NATURE)) {
						LiquibaseNature.addNature(project);
						LiquibaseNature.addBuilder(project);
					}
				} else {
					if (project.hasNature(LiquibaseNature.NATURE)) {
						LiquibaseNature.removeNature(project);
						LiquibaseNature.removeBuilder(project);
					}
				}
			} catch (CoreException e) {
				// TODO Log an error
			}
		}
		// FIXME rebuild projects
		super.okPressed();
	}

}
