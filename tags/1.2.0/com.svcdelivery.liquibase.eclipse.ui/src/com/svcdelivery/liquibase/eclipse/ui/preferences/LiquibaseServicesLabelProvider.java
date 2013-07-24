/**
 * Copyright 2013 Nick Wilson
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
			if (columnIndex == 0) {
				text = ref.getBundle().getSymbolicName();
			} else if (columnIndex == 1) {
				Object versionProperty = ref.getProperty("version");
				if (versionProperty != null) {
					text = versionProperty.toString();
				} else {
					text = "Null version";
				}
			} else {
				text = "Column invalid";
			}
		}
		return text;
	}

}
