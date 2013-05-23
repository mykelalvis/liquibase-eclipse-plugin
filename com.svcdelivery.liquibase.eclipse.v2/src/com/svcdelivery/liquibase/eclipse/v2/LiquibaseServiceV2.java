package com.svcdelivery.liquibase.eclipse.v2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import liquibase.Liquibase;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.Diff;
import liquibase.diff.DiffResult;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import com.svcdelivery.liquibase.eclipse.api.ChangeSetItem;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

public class LiquibaseServiceV2 implements LiquibaseService {
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
			targetDb.dropDatabaseObjects(schema);
		} catch (LiquibaseException e) {
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
		} catch (LiquibaseException e) {
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
			Diff diff = new Diff(targetDb, schema);
			// diff.addStatusListener(new DiffStatusListener() {
			//
			// @Override
			// public void statusUpdate(String message) {
			// System.out.println(message);
			// }
			// });
			DiffResult diffResult = diff.compare();
			if (target.exists()) {
				target.delete();
			}
			FileOutputStream fos = new FileOutputStream(target);
			PrintStream out = new PrintStream(fos);
			diffResult.printChangeLog(out, targetDb);
			out.flush();
			out.close();
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
					changeLogFile.getParent());
			final DatabaseConnection database = new JdbcConnection(connection);
			final Liquibase lb = new Liquibase(changeLogFile.getName(),
					resourceAccessor, database);
			lb.rollback(count, null);
		} catch (LiquibaseException e) {
			throw new LiquibaseApiException(e);
		}
	}

}
