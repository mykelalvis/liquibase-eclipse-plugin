package com.svcdelivery.liquibase.eclipse.api;

import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.BundleContext;

/**
 * Classes from package "com.svcdelivery.liquibase.eclipse.v3" are loaded from
 * this bundle, all others are loaded from the provided libraries.
 * 
 * @author nick
 * 
 */
public class EmbeddedLibraryClassLoader extends URLClassLoader {

	private BundleContext ctx;

	public EmbeddedLibraryClassLoader(BundleContext ctx, URL[] libraries) {
		super(libraries);
		this.ctx = ctx;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = ctx.getBundle().loadClass(name);
		if (c == null) {
			c = super.findClass(name);
		}
		return c;
	}

}
