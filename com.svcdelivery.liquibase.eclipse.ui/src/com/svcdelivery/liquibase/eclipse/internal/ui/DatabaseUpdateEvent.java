package com.svcdelivery.liquibase.eclipse.internal.ui;

/**
 * An event to indicate the scripts in a database have been changed.
 * 
 * @author nickw
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
