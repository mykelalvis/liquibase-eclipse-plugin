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

/**
 * @author nick
 */
public class LiquibaseResult implements Comparable<LiquibaseResult> {

	/**
	 * Result timestamp.
	 */
	private long timestamp;

	/**
	 * The change set script that was run.
	 */
	private String script;

	/**
	 * The status of the results.
	 */
	private LiquibaseResultStatus status;

	/**
	 * Listeners for changes to the result set.
	 */
	private final List<LiquibaseResultChangeListener> listeners;

	/**
	 * Constructor.
	 */
	public LiquibaseResult() {
		listeners = new ArrayList<LiquibaseResultChangeListener>(1);
	}

	/**
	 * @return the result timestamp.
	 */
	public final long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param changeTimestamp
	 *            the result timestamp.
	 */
	public final void setTimestamp(final long changeTimestamp) {
		timestamp = changeTimestamp;
	}

	/**
	 * @return The change set script that was run.
	 */
	public final String getScript() {
		return script;
	}

	/**
	 * @param changeSetScript
	 *            The change set script that was run.
	 */
	public final void setScript(final String changeSetScript) {
		script = changeSetScript;
	}

	/**
	 * @return The result status.
	 */
	public final LiquibaseResultStatus getStatus() {
		return status;
	}

	/**
	 * @param resultStatus
	 *            The result status.
	 */
	public final void setStatus(final LiquibaseResultStatus resultStatus) {
		status = resultStatus;
		notifyLiquibaseResultChange();
	}

	/**
	 * @param listener
	 *            The change listener.
	 */
	public final void addLiquibaseResultChangeListener(
			final LiquibaseResultChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notify listeners of a change.
	 */
	private void notifyLiquibaseResultChange() {
		final LiquibaseChangeEvent event = new LiquibaseChangeEvent(
				LiquibaseChangeEventType.CHANGE, this);
		for (final LiquibaseResultChangeListener listener : listeners) {
			listener.resultChanged(event);
		}
	}

	@Override
	public final int compareTo(final LiquibaseResult o) {
		return (int) (timestamp - o.timestamp);
	}
}
