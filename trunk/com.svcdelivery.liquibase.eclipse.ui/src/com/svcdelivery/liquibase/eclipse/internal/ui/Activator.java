/**
 * Copyright 2012 Nick Wilson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseApiException;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseProvider;
import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;
import com.svcdelivery.liquibase.eclipse.internal.collections.NotifyingArrayList;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.svcdelivery.liquibase.eclipse.ui"; //$NON-NLS-1$

	private static final String DEFAULT_SERVICE_VERSION = "default-liquibase-service";

	protected static final String VERSION = "version";

	protected static final String COMPATIBILITY = "compatibility";

	/**
	 * The shared instance.
	 */
	private static Activator plugin;

	/**
	 * The version of the default liquibase service.
	 */
	private Version defaultLiquibaseVersion;

	/**
	 * The results from running the last changelog.
	 */
	private LiquibaseResults results;

	/**
	 * Cache of changelog files found in liquibase projects.
	 */
	private ChangeLogCache changeLogCache;

	/**
	 * A set of database update listeners.
	 */
	private Set<DatabaseUpdateListener> databaseUpdateListeners;

	/**
	 * Liquibase service tracker.
	 */
	private ServiceTracker<LiquibaseService, LiquibaseService> lbst;

	private NotifyingArrayList<ServiceReference<LiquibaseService>> services;

	/**
	 * Liquibase service tracker.
	 */
	private ServiceTracker<LiquibaseProvider, LiquibaseProvider> lbpt;

	/**
	 * Reference to the active service.
	 */
	private ServiceReference<LiquibaseService> activeRef;

	/**
	 * The active liquibase service.
	 */
	private LiquibaseService active;

	/**
	 * @param context
	 *            The bundle context.
	 * @throws Exception
	 *             If there was a problem starting the bundle.
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin
	 *      #start(org.osgi.framework.BundleContext )
	 */
	@Override
	public final void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		services = new NotifyingArrayList<ServiceReference<LiquibaseService>>();
		databaseUpdateListeners = new HashSet<DatabaseUpdateListener>();
		changeLogCache = new ChangeLogCache();
		results = new LiquibaseResults();
		for (final LiquibaseResultStatus status : LiquibaseResultStatus
				.values()) {
			registerImage(status.getFileName());
		}
		registerImage("script.gif");
		registerImage("database.gif");
		readPreferences();
		lbst = new ServiceTracker<LiquibaseService, LiquibaseService>(
				context,
				LiquibaseService.class,
				new ServiceTrackerCustomizer<LiquibaseService, LiquibaseService>() {

					@Override
					public LiquibaseService addingService(
							ServiceReference<LiquibaseService> reference) {
						services.add(reference);
						LiquibaseService svc = null;
						Version serviceVersion = getServiceVersion(reference);
						if (serviceVersion != null) {
							System.out.println("Found service version "
									+ serviceVersion);
							Version activeVersion = getServiceVersion(activeRef);
							svc = context.getService(reference);
							if (shouldActivate(defaultLiquibaseVersion,
									activeVersion, serviceVersion)) {
								setActiveLiquibaseService(reference);
								// activeRef = reference;
								// active = svc;
							}
						}
						return svc;
					}

					@Override
					public void modifiedService(
							ServiceReference<LiquibaseService> reference,
							LiquibaseService service) {
					}

					@Override
					public void removedService(
							ServiceReference<LiquibaseService> reference,
							LiquibaseService service) {
						services.remove(reference);
						if (reference.equals(activeRef)) {
							active = null;
							activeRef = null;
							// TODO Use highest version
						}
						context.ungetService(reference);
					}
				});
		lbst.open();
		lbpt = new ServiceTracker<LiquibaseProvider, LiquibaseProvider>(
				context,
				LiquibaseProvider.class,
				new ServiceTrackerCustomizer<LiquibaseProvider, LiquibaseProvider>() {

					@Override
					public LiquibaseProvider addingService(
							ServiceReference<LiquibaseProvider> reference) {
						VersionRange range = getServiceVersionRangeProperty(
								reference, COMPATIBILITY);
						LiquibaseProvider provider = context
								.getService(reference);
						registerLibraries(provider, range);
						return provider;
					}

					@Override
					public void modifiedService(
							ServiceReference<LiquibaseProvider> reference,
							LiquibaseProvider provider) {
						VersionRange range = getServiceVersionRangeProperty(
								reference, COMPATIBILITY);
						unregisterLibraries(range);
						registerLibraries(provider, range);
					}

					@Override
					public void removedService(
							ServiceReference<LiquibaseProvider> reference,
							LiquibaseProvider service) {
						VersionRange range = getServiceVersionRangeProperty(
								reference, COMPATIBILITY);
						unregisterLibraries(range);
					}
				});
		lbpt.open();
	}

	public boolean shouldActivate(Version defaultLiquibaseVersion,
			Version activeVersion, Version serviceVersion) {
		boolean shouldActivate = false;
		if (serviceVersion != null) {
			if (defaultLiquibaseVersion == null) {
				if (activeVersion == null
						|| activeVersion.compareTo(serviceVersion) < 0) {
					shouldActivate = true;
				}
			} else if (!defaultLiquibaseVersion.equals(activeVersion)) {
				if (activeVersion == null
						|| defaultLiquibaseVersion.equals(serviceVersion)
						|| activeVersion.compareTo(serviceVersion) < 0) {
					shouldActivate = true;
				}
			}
		}
		return shouldActivate;
	}

	private void readPreferences() {
		IPreferenceStore store = getPreferenceStore();
		String defaultVersionString = store.getString(DEFAULT_SERVICE_VERSION);
		if (defaultVersionString != null && defaultVersionString.length() > 0) {
			try {
				defaultLiquibaseVersion = Version
						.parseVersion(defaultVersionString);
			} catch (IllegalArgumentException e) {
			}
		}
	}

	private void writePreferences() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(DEFAULT_SERVICE_VERSION,
				defaultLiquibaseVersion.toString());
	}

	/**
	 * @param fileName
	 *            The name of the image file to register.
	 */
	private void registerImage(final String fileName) {
		final ImageRegistry ir = getImageRegistry();
		ir.put(fileName,
				imageDescriptorFromPlugin(PLUGIN_ID, "images/" + fileName));
	}

	/**
	 * @param context
	 *            The bundle context.
	 * @throws Exception
	 *             If there was a problem stopping the bundle.
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin
	 *      #stop(org.osgi.framework.BundleContext )
	 */
	@Override
	public final void stop(final BundleContext context) throws Exception {
		lbpt.close();
		lbst.close();
		setActiveLiquibaseService(null);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * @return The liquibase results.
	 */
	public final LiquibaseResults getResults() {
		return results;
	}

	/**
	 * @return The changelog cache.
	 */
	public final ChangeLogCache getChangeLogCache() {
		return changeLogCache;
	}

	/**
	 * Returns an image from the registry.
	 * 
	 * @param name
	 *            The image name.
	 * @return The image.
	 */
	public static Image getImage(final String name) {
		return plugin.getImageRegistry().get(name);
	}

	/**
	 * @param listener
	 *            The listener to add.
	 */
	public final void addDatabaseUpdateListener(
			final DatabaseUpdateListener listener) {
		databaseUpdateListeners.add(listener);
	}

	/**
	 * @param listener
	 *            The listener to remove.
	 */
	public final void removeDatabaseUpdateListener(
			final DatabaseUpdateListener listener) {
		databaseUpdateListeners.remove(listener);
	}

	/**
	 * Notify listeners of updates to a database.
	 * 
	 * @param event
	 *            The event.
	 */
	public final void notifyDatabaseUpdateListeners(
			final DatabaseUpdateEvent event) {
		for (DatabaseUpdateListener listener : databaseUpdateListeners) {
			listener.databaseUpdated(event);
		}
	}

	/**
	 * @return An array of service references to available liquibase services.
	 */
	public final List<ServiceReference<LiquibaseService>> getLiquibaseServices() {
		return services;
	}

	/**
	 * @return The currently active liquibase service.
	 */
	public final LiquibaseService getActiveLiquibaseService() {
		return active;
	}

	/**
	 * @return The currently active liquibase service.
	 */
	public final void setActiveLiquibaseService(
			ServiceReference<LiquibaseService> ref) {
		BundleContext bundleContext = getBundle().getBundleContext();
		if (activeRef != null && active != null) {
			bundleContext.ungetService(activeRef);
		}
		activeRef = ref;
		if (ref != null) {
			Version serviceVersion = getServiceVersion(ref);
			System.out.println("Setting active version " + serviceVersion);
			active = bundleContext.getService(activeRef);
		}
	}

	public final void setDefaultLiquibaseService(
			ServiceReference<LiquibaseService> newDefaultLiquibaseService) {
		Version newVersion = getServiceVersion(newDefaultLiquibaseService);
		defaultLiquibaseVersion = newVersion == null ? null : newVersion;
		writePreferences();
		setActiveLiquibaseService(newDefaultLiquibaseService);
	}

	/**
	 * @param reference
	 *            The service reference.
	 * @return The service version.
	 */
	private Version getServiceVersion(
			final ServiceReference<LiquibaseService> reference) {
		return getServiceVersionProperty(reference, VERSION);
	}

	/**
	 * @param reference
	 *            The service reference.
	 * @return The service version.
	 */
	private Version getServiceVersionProperty(
			final ServiceReference<?> reference, String key) {
		Version version = null;
		if (reference != null) {
			Object property = reference.getProperty(key);
			if (property instanceof String) {
				version = Version.parseVersion((String) reference
						.getProperty(key));
			}
		}
		return version;
	}

	/**
	 * @param reference
	 *            The service reference.
	 * @return The service version.
	 */
	private VersionRange getServiceVersionRangeProperty(
			final ServiceReference<?> reference, String key) {
		VersionRange version = null;
		if (reference != null) {
			Object property = reference.getProperty(key);
			if (property instanceof String) {
				version = new VersionRange((String) reference.getProperty(key));
			}
		}
		return version;
	}

	public ServiceReference<LiquibaseService> getActiveLiquibaseServiceReference() {
		return activeRef;
	}

	public ServiceReference<LiquibaseProvider> getLiquibaseProvider(
			Version version) {
		ServiceReference<LiquibaseProvider> provider = null;
		if (version != null) {
			ServiceReference<LiquibaseProvider>[] references = lbpt
					.getServiceReferences();
			if (references != null) {
				for (ServiceReference<LiquibaseProvider> reference : references) {
					VersionRange range = getServiceVersionRangeProperty(
							reference, COMPATIBILITY);
					if (range != null && range.includes(version)) {
						provider = reference;
						break;
					}
				}
			}
		}
		return provider;
	}

	public String storeDescriptor(Version version, URL[] urls) {
		String error = null;
		StringBuilder sb = new StringBuilder();
		for (URL url : urls) {
			if (sb.length() != 0) {
				sb.append(",");
			}
			sb.append(url.toString());
		}
		Properties vp = getVersionProprties();
		vp.put(version.toString(), sb.toString());
		try {
			vp.store(new FileWriter(getVersionPropertiesFile()), "");
			error = registerLibrary(version, urls);
		} catch (IOException e) {
			error = e.getMessage();
			e.printStackTrace();
		}
		return error;
	}

	public void removeDescriptor(Version version) {
		Properties vp = getVersionProprties();
		vp.remove(version.toString());
		try {
			unregisterLibrary(version);
			vp.store(new FileWriter(getVersionPropertiesFile()), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Properties getVersionProprties() {
		File file = getVersionPropertiesFile();
		Properties properties = new Properties();
		if (file.exists()) {
			try {
				properties.load(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	private File getVersionPropertiesFile() {
		IPath path = getStateLocation().append("version.properties");
		File file = path.toFile();
		return file;
	}

	private void registerLibraries(LiquibaseProvider provider,
			VersionRange range) {
		Map<Version, URL[]> versions = getCompatiblieLibraries(range);
		for (Entry<Version, URL[]> entry : versions.entrySet()) {
			Version version = entry.getKey();
			URL[] urls = entry.getValue();
			try {
				provider.registerLibrary(version, urls);
			} catch (LiquibaseApiException e) {
				e.printStackTrace();
			}
		}
	}

	private void unregisterLibraries(VersionRange range) {
		Map<Version, URL[]> versions = getCompatiblieLibraries(range);
		for (Version version : versions.keySet()) {
			unregisterLibrary(version);
		}
	}

	private Map<Version, URL[]> getCompatiblieLibraries(VersionRange range) {
		Map<Version, URL[]> versions = new HashMap<Version, URL[]>();
		Properties properties = getVersionProprties();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			Version version = new Version(key);
			if (range.includes(version)) {
				String[] values = value.split(",");
				URL[] urls = new URL[values.length];
				for (int i = 0; i < values.length; i++) {
					try {
						urls[i] = new URL(values[i]);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				versions.put(version, urls);
			}
		}
		return versions;
	}

	private String registerLibrary(Version version, URL[] urls) {
		String error = null;
		ServiceReference<LiquibaseProvider> providerRef = getLiquibaseProvider(version);
		if (providerRef != null) {
			BundleContext ctx = getBundle().getBundleContext();
			LiquibaseProvider provider = null;
			try {
				provider = ctx.getService(providerRef);
				if (provider != null) {
					provider.registerLibrary(version, urls);
				} else {
					error = "Provider unavailable for version " + version;
				}
			} catch (LiquibaseApiException e) {
				e.printStackTrace();
				error = "Error registering library for version " + version;
			} finally {
				if (provider != null) {
					ctx.ungetService(providerRef);
				}
			}
		} else {
			error = "No provider found for version " + version;
		}
		return error;
	}

	private String unregisterLibrary(Version version) {
		String error = null;
		ServiceReference<LiquibaseProvider> providerRef = getLiquibaseProvider(version);
		if (providerRef != null) {
			BundleContext ctx = getBundle().getBundleContext();
			LiquibaseProvider provider = null;
			try {
				provider = ctx.getService(providerRef);
				if (provider != null) {
					provider.unregisterLibrary(version);
				} else {
					error = "Provider unavailable for version " + version;
				}
			} catch (LiquibaseApiException e) {
				e.printStackTrace();
				error = "Error unregistering library for version " + version;
			} finally {
				if (provider != null) {
					ctx.ungetService(providerRef);
				}
			}
		} else {
			error = "No provider found for version " + version;
		}
		return error;
	}
}
