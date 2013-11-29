package com.svcdelivery.liquibase.eclipse.api;

import org.osgi.framework.Version;

/**
 * A provider of generic LiquibaseService services for a liquibase version.
 * 
 * @author nick
 */
public interface LiquibaseProvider {
	/**
	 * @param library
	 *            The full path of the library file.
	 * @param version
	 *            The version to register.
	 * @throws LiquibaseApiException
	 *             if the library could not be registered.
	 */
	void registerLibrary(String library, Version version)
			throws LiquibaseApiException;

	/**
	 * @param version
	 *            The version to register.
	 * @throws LiquibaseApiException
	 *             if the library could not be registered.
	 */
	void unregisterLibrary(Version version) throws LiquibaseApiException;
}
