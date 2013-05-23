package com.svcdelivery.liquibase.eclipse.v3;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import com.svcdelivery.liquibase.eclipse.api.ChangeSetItem;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

public class LiquibaseServiceV3 implements LiquibaseService {
	@Override
	public List<ChangeSetItem> getRanChangeSets(Connection connection)
			throws LiquibaseApiException {
		List<ChangeSetItem> items = new ArrayList<ChangeSetItem>();
		try {
			final DatabaseConnection database = new JdbcConnection(connection);
			Database targetDb = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(database);
			List<RanChangeSet> changeSets = targetDb.getRanChangeSetList();
			for (RanChangeSet changeSet : changeSets) {
				ChangeSetItem item = new ChangeSetItem();
				item.setAuthor(changeSet.getAuthor());
				item.setChangeLog(changeSet.getChangeLog());
				item.setDateExecuted(changeSet.getDateExecuted());
				item.setExecType(changeSet.getExecType().name());
				item.setId(changeSet.getId());
				item.setLastCheckSum(changeSet.getLastCheckSum().toString());
				item.setTag(changeSet.getTag());
				items.add(item);
			}
		} catch (LiquibaseException e) {
			throw new LiquibaseApiException(e);
		}
		return items;
	}

	@Override
	public void dropAll(Connection connection, String schema)
			throws LiquibaseApiException {
		try {
			final DatabaseConnection database = new JdbcConnection(connection);
			Database targetDb = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(database);
			CatalogAndSchema cas = new CatalogAndSchema(schema, schema);
			targetDb.dropDatabaseObjects(cas);
		} catch (Exception e) {
			throw new LiquibaseApiException(e);
		}
	}

	public void update(final File changeLogFile, final Connection connection)
			throws LiquibaseApiException {
		try {
			final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
					changeLogFile.getParent());
			final DatabaseConnection database = new JdbcConnection(connection);
			final Liquibase lb = new Liquibase(changeLogFile.getName(),
					resourceAccessor, database);
			lb.update(null);
		} catch (Exception e) {
			throw new LiquibaseApiException(e);
		}
	}

	@Override
	public void diff(final Connection connection, final String schema,
			final File target) throws LiquibaseApiException {
		try {
			final DatabaseConnection database = new JdbcConnection(connection);
			Database targetDb = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(database);
			DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(
					targetDb, null, null);
			if (target.exists()) {
				target.delete();
			}
			DiffOutputControl diffOutputControl = new DiffOutputControl();
			DiffToChangeLog dtc = new DiffToChangeLog(diffResult,
					diffOutputControl);
			dtc.print(target.getPath());
		} catch (LiquibaseException e) {
			throw new LiquibaseApiException(e);
		} catch (IOException e) {
			throw new LiquibaseApiException(e);
		} catch (ParserConfigurationException e) {
			throw new LiquibaseApiException(e);
		}
	}

	@Override
	public void rollback(File changeLogFile, int count, Connection connection)
			throws LiquibaseApiException {
		try {
			final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
					changeLogFile.toString());
			final DatabaseConnection database = new JdbcConnection(connection);
			final Liquibase lb = new Liquibase(changeLogFile.getName(),
					resourceAccessor, database);
			lb.rollback(count, null);
		} catch (LiquibaseException e) {
			throw new LiquibaseApiException(e);
		}
	}

}
