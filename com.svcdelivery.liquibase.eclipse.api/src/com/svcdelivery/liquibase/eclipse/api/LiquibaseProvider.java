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
	 * @param version
	 *            The version to register.
	 * @param libraries
	 *            The URLs of the library files.
	 * @throws LiquibaseApiException
	 *             if the library could not be registered.
	 */
	void registerLibrary(Version version, URL[] libraries)
			throws LiquibaseApiException;

	/**
	 * @param version
	 *            The version to register.
	 * @throws LiquibaseApiException
	 *             if the library could not be registered.
	 */
	void unregisterLibrary(Version version) throws LiquibaseApiException;

	/**
	 * @param version
	 *            The library version.
	 * @return A list of jar URLs.
	 */
	URL[] getLibraries(Version version);
}
