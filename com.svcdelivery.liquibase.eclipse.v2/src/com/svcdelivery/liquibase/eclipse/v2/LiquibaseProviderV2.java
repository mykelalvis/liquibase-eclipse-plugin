package com.svcdelivery.liquibase.eclipse.v2;

import java.net.URL;

import org.osgi.framework.Version;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseProvider;

public class LiquibaseProviderV2 implements LiquibaseProvider {

	@Override
	public void registerLibrary(URL[] library, Version version)
			throws LiquibaseApiException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterLibrary(Version version) throws LiquibaseApiException {
		// TODO Auto-generated method stub

	}

}
