package com.svcdelivery.liquibase.eclipse.v2;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;

import com.svcdelivery.liquibase.eclipse.api.EmbeddedLibraryClassLoader;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseProvider;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

public class LiquibaseProviderV2 implements LiquibaseProvider {

	private BundleContext ctx;

	private Map<Version, ServiceRegistration<LiquibaseService>> register;

	private Map<Version, URL[]> versionLibraries;

	public void activate(BundleContext ctx) {
		this.ctx = ctx;
		register = new HashMap<Version, ServiceRegistration<LiquibaseService>>();
		versionLibraries = new HashMap<Version, URL[]>();
	}

	/**
	 * TODO: Convert liquibasev2 DS to a factory so that it gets registered like
	 * other service providers with the right classloader.
	 * TODO: Ensure DS providers get additional libs added to them.
	 * 
	 * @see com.svcdelivery.liquibase.eclipse.api.LiquibaseProvider#registerLibrary(org.osgi.framework.Version,
	 *      java.net.URL[])
	 */
	@Override
	public void registerLibrary(Version version, URL[] libraries)
			throws LiquibaseApiException {
		if (versionLibraries.containsKey(version)) {
			// TODO Is service already registered? Replace or Error?
		} else {
			versionLibraries.put(version, libraries);
			ClassLoader cl;
			if (version.toString().equals("2.0.5")) {
				// FIXME dynamically detect embedded version numbers.
				// TODO Is it an embedd? Contribute additional libraries
				cl = new EmbeddedLibraryClassLoader(ctx, libraries);
			} else {
				cl = new GenericLibraryClassLoader(ctx, libraries);
			}
			try {
				Class<?> c = cl.loadClass(LiquibaseServiceV2.class.getName());
				if (LiquibaseService.class.isAssignableFrom(c)) {
					LiquibaseService service = (LiquibaseService) c
							.newInstance();
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put("version", version.toString());
					ServiceRegistration<LiquibaseService> reg = ctx
							.registerService(LiquibaseService.class, service,
									properties);
					register.put(version, reg);
				}
			} catch (ClassNotFoundException e) {
				throw new LiquibaseApiException("Class not found "
						+ e.getMessage());
			} catch (InstantiationException e) {
				throw new LiquibaseApiException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new LiquibaseApiException(e.getMessage());
			}
		}
	}

	@Override
	public void unregisterLibrary(Version version) throws LiquibaseApiException {
		ServiceRegistration<LiquibaseService> reg = register.get(version);
		if (reg != null) {
			register.remove(version);
			reg.unregister();
		}
		versionLibraries.remove(version);
	}

	@Override
	public URL[] getLibraries(Version version) {
		return versionLibraries.get(version);
	}

	@Override
	public void addLibrary(Version version, URL url) {
		URL[] current = versionLibraries.get(version);
		if (current == null) {
			current = new URL[] { url };
		} else {
			current = Arrays.copyOf(current, current.length + 1);
			current[current.length - 1] = url;
		}
		versionLibraries.put(version, current);
	}

	@Override
	public void removeLibrary(Version version, URL url) {
		URL[] current = versionLibraries.get(version);
		if (current != null) {
			current = Arrays.copyOf(current, current.length - 1);
			versionLibraries.put(version, current);
		}
	}

}
