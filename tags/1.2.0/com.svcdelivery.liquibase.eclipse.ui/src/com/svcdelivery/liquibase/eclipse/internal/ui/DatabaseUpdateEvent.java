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

/**
 * An event to indicate the scripts in a database have been changed.
 * 
 * @author nick
 * 
 */
public class DatabaseUpdateEvent {

	/**
	 * The element that has changed.
	 */
	private Object element;

	/**
	 * @param changedElement
	 *            The element that has changed.
	 */
	public DatabaseUpdateEvent(final Object changedElement) {
		element = changedElement;
	}

	/**
	 * @return The element that has changed.
	 */
	public final Object getElement() {
		return element;
	}

}
