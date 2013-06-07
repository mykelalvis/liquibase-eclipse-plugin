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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author nick
 */
public class ChangeSetLabelProvider implements ITableLabelProvider {

	/**
	 * Date formatter.
	 */
	private final SimpleDateFormat df;

	/**
	 * Constructor.
	 */
	public ChangeSetLabelProvider() {
		df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}

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
	public final Image getColumnImage(final Object element, final int column) {
		return null;
	}

	@Override
	public final String getColumnText(final Object element, final int column) {
		String text = "";
		// if (element instanceof RanChangeSet) {
		// final RanChangeSet cs = (RanChangeSet) element;
		// if (column == 0) {
		// text = cs.getChangeLog();
		// } else if (column == 1) {
		// text = df.format(cs.getDateExecuted());
		// }
		// } else
		if (element instanceof ChangeSetTreeItem) {
			ChangeSetTreeItem item = (ChangeSetTreeItem) element;
			if (column == 0) {
				text = item.getChangeLog();
			} else if (column == 1) {
				Date dateExecuted = item.getDateExecuted();
				if (dateExecuted != null) {
					text = df.format(dateExecuted);
				}
			} else if (column == 2) {
				IFile changeLogFile = item.getChangeLogFile();
				if (changeLogFile != null) {
					text = changeLogFile.getProjectRelativePath().toString();
				}
			} else if (column == 2) {
				IFile changeLogFile = item.getChangeLogFile();
				if (changeLogFile != null) {
					text = "Located";
				} else {
					text = "Not found";
				}
			}
		}
		return text;
	}
}
