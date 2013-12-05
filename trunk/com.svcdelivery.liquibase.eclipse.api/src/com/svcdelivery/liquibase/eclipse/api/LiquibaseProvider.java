package com.svcdelivery.liquibase.eclipse.api;

import java.net.URL;

import org.osgi.framework.Version;

/**
 * A provider of generic LiquibaseService services for a liquibase version.
 * 
 * @author nick
 */
public interface LiquibaseProvider {
	/**
	 * @param libraries
	 *            The URLs of the library files.
	 * @param version
	 *            The version to register.
	 * @throws LiquibaseApiException
	 *             if the library could not be registered.
	 */
	void registerLibrary(URL[] libraries, Version version)
			throws LiquibaseApiException;

	/**
	 * @param version
	 *            The version to register.
	 * @throws LiquibaseApiException
	 *             if the library could not be registered.
	 */
	void unregisterLibrary(Version version) throws LiquibaseApiException;
}
