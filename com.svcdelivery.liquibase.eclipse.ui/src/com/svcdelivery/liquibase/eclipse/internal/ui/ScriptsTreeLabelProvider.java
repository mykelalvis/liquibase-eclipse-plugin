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

import liquibase.changelog.DatabaseChangeLog;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author nick
 */
public class ScriptsTreeLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(final ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public final boolean isLabelProperty(final Object element,
			final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}

	@Override
	public final Image getColumnImage(final Object element,
			final int columnIndex) {
		Image image = null;
		if (columnIndex == 0) {
			image = Activator.getImage("script.gif");
		}
		return image;
	}

	@Override
	public final String getColumnText(final Object element,
			final int columnIndex) {
		String text = "";
		if (element instanceof DatabaseChangeLog) {
			final DatabaseChangeLog log = (DatabaseChangeLog) element;
			if (columnIndex == 0) {
				text = log.getFilePath();
			}
		}
		return text;
	}

}
