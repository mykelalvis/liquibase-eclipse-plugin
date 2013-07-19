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
package com.svcdelivery.liquibase.eclipse.v3;

import java.util.Set;

import liquibase.servicelocator.DefaultPackageScanClassResolver;
import liquibase.servicelocator.PackageScanFilter;

import org.osgi.framework.Bundle;

import com.svcdelivery.liquibase.eclipse.api.JarClassIndexer;

/**
 * Package scan resolver.
 */
public class EmbeddedJarPackageScanClassResolver extends
		DefaultPackageScanClassResolver {

	private final Bundle bundle;

	private final JarClassIndexer indexer;

	public EmbeddedJarPackageScanClassResolver(Bundle bundle) {
		this.bundle = bundle;
		indexer = new JarClassIndexer();
		indexer.addJar(bundle.getEntry("/lib/liquibase.jar"));
		indexer.addJar(bundle.getEntry("/lib/snakeyaml-1.12.jar"));
	}

	@Override
	protected void find(final PackageScanFilter test, final String packageName,
			final Set<Class<?>> classes) {
		Set<String> classNames = indexer.getClasses(packageName, true);
		for (String className : classNames) {
			try {
				Class<?> klass = bundle.loadClass(className);
				if (test.matches(klass)) {
					classes.add(klass);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}