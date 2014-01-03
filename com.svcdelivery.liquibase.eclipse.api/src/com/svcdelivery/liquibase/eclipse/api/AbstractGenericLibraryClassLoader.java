package com.svcdelivery.liquibase.eclipse.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
public abstract class AbstractGenericLibraryClassLoader extends URLClassLoader {

	private BundleContext ctx;

	public AbstractGenericLibraryClassLoader(BundleContext ctx, URL[] libraries) {
		super(libraries);
		this.ctx = ctx;
	}

	protected abstract String getPackage();

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = null;
		if (name.startsWith(getPackage())) {
			String clsName = name.replace(".", "/") + ".class";
			URL url = ctx.getBundle().getResource(clsName);
			if (url != null) {
				try {
					InputStream is = url.openStream();
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					int count;
					byte[] buffer = new byte[1024];
					while ((count = is.read(buffer)) != -1) {
						os.write(buffer, 0, count);
					}
					is.close();
					os.close();
					byte[] data = os.toByteArray();
					c = defineClass(name, data, 0, data.length);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ClassNotFoundException(name);
				}
			} else {
				throw new ClassNotFoundException(name);
			}
		} else if (name.startsWith("com.svcdelivery.liquibase.eclipse")) {
			c = ctx.getBundle().loadClass(name);
		} else {
			c = super.findClass(name);
		}
		return c;
	}

}
