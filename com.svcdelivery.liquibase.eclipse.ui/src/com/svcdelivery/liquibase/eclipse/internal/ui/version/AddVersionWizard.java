package com.svcdelivery.liquibase.eclipse.internal.ui.version;

import org.eclipse.jface.wizard.Wizard;

public class AddVersionWizard extends Wizard{

	@Override
	public void addPages() {
		addPage(new LibrarySelectorPage());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
