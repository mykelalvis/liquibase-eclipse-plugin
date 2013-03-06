package com.svcdelivery.liquibase.eclipse.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;

public class GenerateScriptWizard extends Wizard {

	private IResource selected;
	/**
	 * The data source selection page.
	 */
	private DataSourcePage dataSourcePage;

	private SchemaPickerPage schemaPickerPage;

	public GenerateScriptWizard(IResource selected) {
		this.selected = selected;
	}

	@Override
	public final void addPages() {
		dataSourcePage = new DataSourcePage(SWT.SINGLE);
		schemaPickerPage = new SchemaPickerPage();
		dataSourcePage.addPageCompleteListener(schemaPickerPage);
		addPage(dataSourcePage);
		addPage(schemaPickerPage);
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
