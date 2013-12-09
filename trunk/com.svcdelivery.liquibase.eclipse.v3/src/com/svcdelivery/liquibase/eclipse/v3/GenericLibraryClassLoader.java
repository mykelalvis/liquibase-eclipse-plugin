package com.svcdelivery.liquibase.eclipse.v3;

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
public class GenericLibraryClassLoader extends ClassLoader {

	private BundleContext ctx;
	private URLClassLoader urlc;

	public GenericLibraryClassLoader(BundleContext ctx, URL[] libraries) {
		this.ctx = ctx;
		urlc = new URLClassLoader(libraries);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = null;
		if (name.startsWith("com.svcdelivery.liquibase.eclipse.v3")) {
			c = ctx.getBundle().loadClass(name);
		}
		if (c == null) {
			c = urlc.loadClass(name);
		}
		return c;
	}

}
