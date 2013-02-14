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

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author nick
 */
public class ConnectionProfileContentProvider implements
		IStructuredContentProvider {
	/**
	 * Connection profiles.
	 */
	private IConnectionProfile[] profiles = new IConnectionProfile[0];

	/**
	 * Connection profile comparator.
	 */
	private Comparator<IConnectionProfile> comparator;

	/**
	 * Constructor.
	 */
	public ConnectionProfileContentProvider() {
		comparator = new Comparator<IConnectionProfile>() {

			@Override
			public int compare(final IConnectionProfile p1,
					final IConnectionProfile p2) {
				return p1.getName().compareTo(p2.getName());
			}
		};
	}

	@Override
	public final void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		if (newInput instanceof IConnectionProfile[]) {
			profiles = (IConnectionProfile[]) newInput;
			Arrays.sort(profiles, comparator);
		}
	}

	@Override
	public final Object[] getElements(final Object inputElement) {
		return profiles;
	}

	@Override
	public void dispose() {
	}

}
