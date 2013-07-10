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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

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
						LiquibaseService svc = null;
						Version serviceVersion = getServiceVersion(reference);
						if (serviceVersion != null) {
							System.out.println("Found service version "
									+ serviceVersion);
							Version activeVersion = getServiceVersion(activeRef);
							svc = context.getService(reference);
							// If
							// - This is the default
							// - OR the active service is null

							// - OR (the active service is not the default AND
							// this
							// service has a higher version number)
							if ((defaultLiquibaseVersion != null && defaultLiquibaseVersion
									.equals(serviceVersion))
									|| activeRef == null
									|| (activeVersion != null && serviceVersion
											.compareTo(activeVersion) > 0)) {
								System.out.println("Setting active version "
										+ serviceVersion);
								activeRef = reference;
								active = svc;
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
						if (reference.equals(activeRef)) {
							active = null;
							activeRef = null;
							// TODO Use highest version
						}
						context.ungetService(reference);
					}
				});
		lbst.open();
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
	public final ServiceReference<LiquibaseService>[] getLiquibaseServices() {
		return lbst.getServiceReferences();
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
		Version version = null;
		if (reference != null) {
			Object property = reference.getProperty(VERSION);
			if (property instanceof String) {
				version = Version.parseVersion((String) reference
						.getProperty(VERSION));
			}
		}
		return version;
	}

	public ServiceReference<LiquibaseService> getActiveLiquibaseServiceReference() {
		return activeRef;
	}

	private void addSchema() {
	}

}
