package com.svcdelivery.liquibase.eclipse.v3;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import com.svcdelivery.liquibase.eclipse.api.ChangeSetItem;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

public class GenericServiceV3 implements LiquibaseService {

	private LiquibaseService proxy;
	
	public GenericServiceV3(String library) {

	}

	@Override
	public List<ChangeSetItem> getRanChangeSets(Connection connection)
			throws LiquibaseApiException {
		return proxy.getRanChangeSets(connection);
	}

	@Override
	public void dropAll(Connection connection, String schema)
			throws LiquibaseApiException {
		proxy.dropAll(connection, schema);
	}

	@Override
	public void update(File changeLogFile, Connection connection)
			throws LiquibaseApiException {
		proxy.update(changeLogFile, connection);
	}

	@Override
	public void diff(Connection connection, String schema, File target)
			throws LiquibaseApiException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback(File changeLogFile, int count, Connection connection)
			throws LiquibaseApiException {
		// TODO Auto-generated method stub

	}

}
