package com.svcdelivery.liquibase.eclipse.internal.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class TargetFilePage extends WizardPage {

	private TreeViewer targetTree;

	private Text targetFile;

	private IContainer targetParent;

	private String targetFilename;

	/**
	 * Constructor to allow change set item to be set later.
	 */
	public TargetFilePage() {
		super("Select Target File");
		setTitle("Select Target File");
		setMessage("Select a target folder and filename then click finish to generate a changelog script.");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, false));
		targetTree = new TreeViewer(root);
		targetTree.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		targetTree.setContentProvider(new BaseWorkbenchContentProvider());
		targetTree.setLabelProvider(new WorkbenchLabelProvider());
		targetTree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				targetParent = null;
				targetFilename = null;
				ISelection selection = targetTree.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection structured = (StructuredSelection) selection;
					if (structured.size() == 1) {
						Object first = structured.getFirstElement();
						if (first instanceof IResource) {
							IResource resource = (IResource) first;
							if (resource instanceof IFile) {
								targetParent = resource.getParent();
								targetFilename = resource.getName();
							} else {
								targetParent = (IContainer) resource;
							}
						}
					}
				}
				setPageComplete(targetParent != null && targetFilename != null);
				updateControls();
			}
		});
		targetTree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		Label targetFileLabel = new Label(root, SWT.NONE);
		targetFileLabel.setText("Target Filename:");
		targetFileLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		targetFile = new Text(root, SWT.NONE);
		targetFile
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		updateControls();
		setControl(root);
	}

	public IContainer getTargetContainer() {
		return targetParent;
	}

	protected void updateControls() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				ISelection selection = new StructuredSelection(targetParent);
				targetTree.setSelection(selection, true);
				targetFile.setText(targetFilename);
			}
		});
	}

	public String getFilename() {
		return targetFilename;
	}

}
