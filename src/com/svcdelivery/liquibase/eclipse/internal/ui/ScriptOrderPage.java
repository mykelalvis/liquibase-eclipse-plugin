package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ScriptOrderPage extends WizardPage {

	/**
	 * The script files to run.
	 */
	private List<IFile> scriptFiles;
	private ListViewer fileViewer;
	private Button up;
	private Button down;

	public ScriptOrderPage(List<IFile> files) {
		super("Script Order");
		setTitle("Select the order in which to run the scripts.");
		scriptFiles = files;
	}

	@Override
	public void createControl(Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, false));

		fileViewer = new ListViewer(root);
		fileViewer.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		fileViewer.setContentProvider(new CollectionContentProvider());
		fileViewer.setLabelProvider(new WorkbenchLabelProvider());
		fileViewer.setInput(scriptFiles);
		fileViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateButtonStates();
			}
		});

		Composite buttonPanel = new Composite(root, SWT.NONE);
		buttonPanel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		buttonPanel.setLayout(new RowLayout(SWT.VERTICAL));
		up = new Button(buttonPanel, SWT.PUSH);
		up.setImage(Activator.getImage("up"));
		down = new Button(buttonPanel, SWT.PUSH);
		down.setImage(Activator.getImage("down"));

		up.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IFile selected = getSelectedFile();
				int index = scriptFiles.indexOf(selected);
				if (index != -1) {
					IFile other = scriptFiles.get(index - 1);
					scriptFiles.set(index - 1, selected);
					scriptFiles.set(index, other);
					fileViewer.refresh();
				}
				updateButtonStates();
			}
		});
		down.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IFile selected = getSelectedFile();
				int index = scriptFiles.indexOf(selected);
				if (index != -1) {
					IFile other = scriptFiles.get(index + 1);
					scriptFiles.set(index + 1, selected);
					scriptFiles.set(index, other);
					fileViewer.refresh();
				}
				updateButtonStates();
			}
		});

		setControl(root);
	}

	protected void updateButtonStates() {
		boolean upState = false;
		boolean downState = false;
		IFile selected = getSelectedFile();
		if (selected != null) {
			if (!selected.equals(scriptFiles.get(0))) {
				upState = true;
			}
			if (!selected.equals(scriptFiles.get(scriptFiles.size() - 1))) {
				downState = true;
			}
		}
		up.setEnabled(upState);
		down.setEnabled(downState);
	}

	private IFile getSelectedFile() {
		StructuredSelection selection = (StructuredSelection) fileViewer
				.getSelection();
		IFile selected = (IFile) selection.getFirstElement();
		return selected;
	}

}
