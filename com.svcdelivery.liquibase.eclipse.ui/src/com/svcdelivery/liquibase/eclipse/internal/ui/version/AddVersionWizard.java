package com.svcdelivery.liquibase.eclipse.internal.ui.version;

import java.net.URL;

import org.eclipse.jface.wizard.Wizard;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseProvider;
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
		boolean success = false;
		libarySelectorPage.setErrorMessage(null);
		URL[] urls = libarySelectorPage.getURLs();
		Version version = libarySelectorPage.getVersion();
		Activator activator = Activator.getDefault();
		ServiceReference<LiquibaseProvider> providerRef = activator
				.getLiquibaseProvider(version);
		if (providerRef != null) {
			BundleContext ctx = activator.getBundle().getBundleContext();
			LiquibaseProvider provider = null;
			try {
				provider = ctx.getService(providerRef);
				if (provider != null) {
					provider.registerLibrary(urls, version);
				} else {
					libarySelectorPage
							.setErrorMessage("Provider unavailable for version "
									+ version);
				}
			} catch (LiquibaseApiException e) {
				e.printStackTrace();
				libarySelectorPage
						.setErrorMessage("Error registering library for version "
								+ version);
			} finally {
				if (provider != null) {
					ctx.ungetService(providerRef);
				}
			}
		} else {
			libarySelectorPage.setErrorMessage("No provider found for version "
					+ version);
		}
		return success;
	}

}
