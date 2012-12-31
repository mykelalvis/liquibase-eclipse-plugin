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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * @author nick
 */
public class DataSourcePage extends WizardPage {

	/**
	 * The selected connection profile.
	 */
	private IConnectionProfile[] profiles;
	/**
	 * A viewer to show available connection profiles.
	 */
	private CheckboxTableViewer profilePicker;
	/**
	 * Flag to indicate when the user is navigating to the next page, used for
	 * triggering events.
	 */
	private boolean changingPage = true;
	/**
	 * A list of listeners for page completion events.
	 */
	private final List<CompleteListener> listeners;

	/**
	 * Styles to apply to picker.
	 */
	private int style;

	/**
	 * Constructor.
	 */
	protected DataSourcePage(final int style) {
		super("Data Source");
		this.style = style;
		setTitle("Data Source");
		setMessage("Select the Data Source to run the scripts against.");
		listeners = new ArrayList<CompleteListener>();
		setPageComplete(false);
	}

	@Override
	public final void createControl(final Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new FillLayout());

		Table profilePickerTable = new Table(root, SWT.V_SCROLL | SWT.CHECK
				| style);
		profilePicker = new CheckboxTableViewer(profilePickerTable);
		profilePicker
				.setContentProvider(new ConnectionProfileContentProvider());
		profilePicker.setLabelProvider(new ConnectionProfileLabelProvider());
		profilePicker.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object[] elements = profilePicker.getCheckedElements();
				List<IConnectionProfile> profileList = new ArrayList<IConnectionProfile>();
				for (Object next : elements) {
					if (next instanceof IConnectionProfile) {
						profileList.add((IConnectionProfile) next);
					}
				}
				profiles = profileList
						.toArray(new IConnectionProfile[profileList.size()]);
				updatePageComplete();
			}
		});
		profilePicker.setInput(ProfileManager.getInstance().getProfiles());
		setControl(root);
	}

	/**
	 * Update the page complete status.
	 */
	private void updatePageComplete() {
		final boolean ok = profiles != null && profiles.length > 0;
		setPageComplete(ok);
	}

	/**
	 * @return The selected connection profile.
	 */
	public final IConnectionProfile[] getProfiles() {
		return profiles;
	}

	/**
	 * @return The selected connection profile.
	 */
	public final IConnectionProfile getProfile() {
		return profiles[0];
	}

	@Override
	public final boolean canFlipToNextPage() {
		changingPage = false;
		final boolean canFlip = super.canFlipToNextPage();
		changingPage = true;
		return canFlip;
	}

	@Override
	public final IWizardPage getNextPage() {
		if (changingPage) {
			final Thread notify = new Thread(new Runnable() {

				@Override
				public void run() {
					for (final CompleteListener listener : listeners) {
						listener.complete(true, getProfile());
					}
				}
			});
			notify.start();
		}
		return super.getNextPage();
	}

	/**
	 * @param listener
	 *            The page complete listener.
	 */
	public final void addPageCompleteListener(final CompleteListener listener) {
		listeners.add(listener);
	}
}
