package com.svcdelivery.liquibase.eclipse.internal.ui;

/**
 * A listener for changes to the scripts in a database.
 * 
 * @author nickw
 * 
 */
public interface DatabaseUpdateListener {
	/**
	 * @param event
	 *            The event to indicate what has changed.
	 */
	void databaseUpdated(DatabaseUpdateEvent event);
}
