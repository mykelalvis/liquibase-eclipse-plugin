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
package com.svcdelivery.liquibase.eclipse.internal.ui.version;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author nick
 */
public class LibrarySelectorPage extends WizardPage {

	private Text url;

	private ComboViewer apiVersion;

	/**
	 * Constructor.
	 */
	protected LibrarySelectorPage() {
		super("Select Library");
		setTitle("Select Library");
		setMessage("Enter the URL of a Liquibase jar file and specify the API version that it implements.");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, false));

		Label urlLabel = new Label(root, SWT.NONE);
		urlLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
		url = new Text(root, SWT.NONE);
		url.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label apiLabel = new Label(root, SWT.NONE);
		apiLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
		apiVersion = new ComboViewer(root, SWT.NONE);
		apiVersion.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		apiVersion.setContentProvider(new ApiVersionContentProvider());
	}

}
