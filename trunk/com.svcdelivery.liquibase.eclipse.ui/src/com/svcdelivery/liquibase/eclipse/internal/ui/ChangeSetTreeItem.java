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

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.connectivity.IConnectionProfile;

import com.svcdelivery.liquibase.eclipse.api.ChangeSetItem;

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
	private ChangeSetItem changeSet;

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
	public final ChangeSetItem getChangeSet() {
		return changeSet;
	}

	/**
	 * @param ranChangeSet
	 *            Change set.
	 */
	public final void setChangeSet(final ChangeSetItem ranChangeSet) {
		changeSet = ranChangeSet;
		String name = changeSet.getChangeLog();
		ChangeLogCache cache = Activator.getDefault().getChangeLogCache();
		file = cache.getFile(name);
	}

	/**
	 * @return The workspace file matching the change log.
	 */
	public final IFile getChangeLogFile() {
		return file;
	}

	@Override
	public final int hashCode() {
		return profile.hashCode() + changeSet.hashCode();
	}

	@Override
	public final boolean equals(final Object other) {
		boolean equal = false;
		if (other instanceof ChangeSetTreeItem) {
			ChangeSetTreeItem csti = (ChangeSetTreeItem) other;
			equal = profile.equals(csti.profile)
					&& changeSet.equals(csti.changeSet);
		}
		return equal;
	}

	public void setChangeLogFile(IFile file) {
		this.file = file;
	}

	public String getChangeLog() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDateExecuted() {
		// TODO Auto-generated method stub
		return null;
	}

}
