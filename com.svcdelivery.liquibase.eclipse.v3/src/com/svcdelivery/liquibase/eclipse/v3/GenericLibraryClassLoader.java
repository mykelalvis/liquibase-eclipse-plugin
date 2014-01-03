package com.svcdelivery.liquibase.eclipse.v3;

import java.net.URL;

import org.osgi.framework.BundleContext;

import com.svcdelivery.liquibase.eclipse.api.AbstractGenericLibraryClassLoader;

/**
 * Classes from package "com.svcdelivery.liquibase.eclipse.v3" are loaded from
 * this bundle, all others are loaded from the provided libraries.
 * 
 * @author nick
 * 
 */
public class GenericLibraryClassLoader extends
		AbstractGenericLibraryClassLoader {

	public GenericLibraryClassLoader(BundleContext ctx, URL[] libraries) {
		super(ctx, libraries);
	}

	@Override
	protected String getPackage() {
		return "com.svcdelivery.liquibase.eclipse.v3";
	}

}
