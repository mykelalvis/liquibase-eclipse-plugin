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
import java.util.Set;
import java.util.TreeSet;

/**
 * @author nick
 */
public class LiquibaseResults implements LiquibaseResultChangeListener {

	/**
	 * List of liquibase script results.
	 */
	private final Set<LiquibaseResult> results;

	/**
	 * Listeners for changes to the list.
	 */
	private final List<LiquibaseResultChangeListener> listeners;

	/**
	 * Constructor.
	 */
	public LiquibaseResults() {
		results = new TreeSet<LiquibaseResult>();
		listeners = new ArrayList<LiquibaseResultChangeListener>(1);
	}

	/**
	 * @param result
	 *            The result to add.
	 */
	public final void add(final LiquibaseResult result) {
		results.add(result);
		result.addLiquibaseResultChangeListener(this);
		final LiquibaseChangeEvent event = new LiquibaseChangeEvent(
				LiquibaseChangeEventType.ADD, result);
		resultChanged(event);
	}

	/**
	 * @param listener
	 *            The listener to add.
	 */
	public final void addLiquibaseResultChangeListener(
			final LiquibaseResultChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener
	 *            The listener to remove.
	 */
	public final void removeLiquibaseResultChangeListener(
			final LiquibaseResultChangeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public final void resultChanged(final LiquibaseChangeEvent event) {
		for (final LiquibaseResultChangeListener listener : listeners) {
			listener.resultChanged(event);
		}
	}

	/**
	 * @return The list of results.
	 */
	public final Set<LiquibaseResult> getResults() {
		return results;
	}
}
