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

import liquibase.changelog.RanChangeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.connectivity.IConnectionProfile;

/**
 * @author nick
 */
public class ChangeSetTreeItem {
	/**
	 * Connection profile.
	 */
	private IConnectionProfile profile;

	/**
	 * Change set.
	 */
	private RanChangeSet changeSet;

	/**
	 * The workspace file that matches the change set, or null.
	 */
	private IFile file;

	/**
	 * @return Connection profile.
	 */
	public final IConnectionProfile getProfile() {
		return profile;
	}

	/**
	 * @param connectionProfile
	 *            Connection profile.
	 */
	public final void setProfile(final IConnectionProfile connectionProfile) {
		profile = connectionProfile;
	}

	/**
	 * @return Change set.
	 */
	public final RanChangeSet getChangeSet() {
		return changeSet;
	}

	/**
	 * @param ranChangeSet
	 *            Change set.
	 */
	public final void setChangeSet(final RanChangeSet ranChangeSet) {
		changeSet = ranChangeSet;
		// TODO Locate the file in the change log cache.
	}

	/**
	 * @return The workspace file matching the change log.
	 */
	public final IFile getChangeLogFile() {
		return file;
	}

}
