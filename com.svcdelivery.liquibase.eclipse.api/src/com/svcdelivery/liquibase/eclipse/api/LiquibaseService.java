package com.svcdelivery.liquibase.eclipse.api;

import java.io.File;
import java.sql.Connection;
import java.util.List;

/**
 * Interface to provide separation between the Eclipse UI and the Liquibase OSGi
 * bundle so that multiple Liquibase versions can be supported.
 * 
 * @author nick
 * 
 */
public interface LiquibaseService {

	List<ChangeSetItem> getRanChangeSets(Connection connection)
			throws LiquibaseApiException;

	void dropAll(Connection connection, String schema)
			throws LiquibaseApiException;

	void update(File changeLogFile, Connection connection)
			throws LiquibaseApiException;

	void diff(Connection connection, String schema, File target)
			throws LiquibaseApiException;

	void rollback(File changeLogFile, int count, Connection connection)
			throws LiquibaseApiException;
}