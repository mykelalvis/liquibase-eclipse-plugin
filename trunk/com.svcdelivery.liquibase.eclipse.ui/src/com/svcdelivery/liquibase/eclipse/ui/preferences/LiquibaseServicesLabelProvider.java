package com.svcdelivery.liquibase.eclipse.ui.preferences;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.ServiceReference;

public class LiquibaseServicesLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String text = "---";
		if (element instanceof ServiceReference) {
			ServiceReference<?> ref = (ServiceReference<?>) element;
			text = ref.getBundle().getSymbolicName();
			Object versionProperty = ref.getProperty("version");
			if (versionProperty != null) {
				text = text + " " + versionProperty.toString();
			}
		}
		return text;
	}

}
