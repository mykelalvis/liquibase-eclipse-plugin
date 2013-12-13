package com.svcdelivery.liquibase.eclipse.internal.ui.version;

import java.net.URL;

import org.eclipse.jface.wizard.Wizard;
import org.osgi.framework.Version;

import com.svcdelivery.liquibase.eclipse.internal.ui.Activator;

public class AddVersionWizard extends Wizard {

	private LibrarySelectorPage libarySelectorPage;

	@Override
	public void addPages() {
		libarySelectorPage = new LibrarySelectorPage();
		addPage(libarySelectorPage);
	}

	@Override
	public boolean performFinish() {
		libarySelectorPage.setErrorMessage(null);
		URL[] urls = libarySelectorPage.getURLs();
		Version version = libarySelectorPage.getVersion();

		Activator activator = Activator.getDefault();
		String error = activator.storeDescriptor(version, urls);

		libarySelectorPage.setErrorMessage(error);
		return error == null;
	}

}
