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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Version;

/**
 * @author nick
 */
public class LibrarySelectorPage extends WizardPage {

	private Text url;

	private Text versionText;

	private List<URL> urls;

	private Version version;

	/**
	 * Constructor.
	 */
	protected LibrarySelectorPage() {
		super("Select Library");
		setTitle("Select Library");
		setMessage("Enter the URL of a Liquibase jar file and specify the API version that it implements.");
		urls = new ArrayList<URL>();
	}

	@Override
	public void createControl(Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(4, false));

		Label urlLabel = new Label(root, SWT.NONE);
		urlLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
		url = new Text(root, SWT.NONE);
		url.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Button browse = new Button(root, SWT.PUSH);
		browse.setText("File");
		browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		Button add = new Button(root, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Label versionLabel = new Label(root, SWT.NONE);
		versionLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
				false));
		versionText = new Text(root, SWT.NONE);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				3, 1));
		
		add.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = url.getText();
				try {
					URL lib = new URL(text);
					urls.add(lib);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		setControl(root);
	}

	public URL[] getURLs() {
		return urls.toArray(new URL[urls.size()]);
	}

	public Version getVersion() {
		return version;
	}
}
