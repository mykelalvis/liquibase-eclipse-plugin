package com.svcdelivery.liquibase.eclipse.v2;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;

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

	@Override
	public void registerLibrary(Version version, URL[] libraries)
			throws LiquibaseApiException {
		versionLibraries.put(version, libraries);
		ClassLoader cl = new GenericLibraryClassLoader(ctx, libraries);
		try {
			Class<?> c = cl.loadClass(LiquibaseServiceV2.class.getName());
			if (LiquibaseService.class.isAssignableFrom(c)) {
				LiquibaseService service = (LiquibaseService) c.newInstance();
				Hashtable<String, String> properties = new Hashtable<String, String>();
				properties.put("version", version.toString());
				ServiceRegistration<LiquibaseService> reg = ctx
						.registerService(LiquibaseService.class, service,
								properties);
				register.put(version, reg);
			}
		} catch (ClassNotFoundException e) {
			throw new LiquibaseApiException("Class not found " + e.getMessage());
		} catch (InstantiationException e) {
			throw new LiquibaseApiException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new LiquibaseApiException(e.getMessage());
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
