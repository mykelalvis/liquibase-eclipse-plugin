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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.svcdelivery.liquibase.eclipse.ui"; //$NON-NLS-1$

	/**
	 * The shared instance.
	 */
	private static Activator plugin;

	/**
	 * The results from running the last changelog.
	 */
	private LiquibaseResults results;

	/**
	 * Cache of changelog files found in liquibase projects.
	 */
	private ChangeLogCache changeLogCache;

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
		changeLogCache = new ChangeLogCache();
		results = new LiquibaseResults();
		for (final LiquibaseResultStatus status : LiquibaseResultStatus
				.values()) {
			registerImage(status.getFileName());
		}
		registerImage("script.gif");
		registerImage("database.gif");

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

}
