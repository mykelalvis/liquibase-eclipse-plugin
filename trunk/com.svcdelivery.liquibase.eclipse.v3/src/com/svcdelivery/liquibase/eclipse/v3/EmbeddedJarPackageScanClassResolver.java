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

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import liquibase.servicelocator.DefaultPackageScanClassResolver;
import liquibase.servicelocator.PackageScanFilter;

import org.osgi.framework.Bundle;

/**
 * Package scan resolver.
 */
public class EmbeddedJarPackageScanClassResolver extends
		DefaultPackageScanClassResolver {

	private final Bundle bundle;

	public EmbeddedJarPackageScanClassResolver(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	protected void find(final PackageScanFilter test, final String packageName,
			final Set<Class<?>> classes) {
		String packagePath = packageName.replace('.', '/');

		scanJar(test, classes, packagePath, "/lib/liquibase.jar");
		scanJar(test, classes, packagePath, "/lib/snakeyaml-1.12.jar");

	}

	private void scanJar(final PackageScanFilter test,
			final Set<Class<?>> classes, String packagePath, String jarfile) {
		URL url = bundle.getEntry(jarfile);
		try {
			JarInputStream jis = new JarInputStream(url.openStream());
			JarEntry next;
			while ((next = jis.getNextJarEntry()) != null) {
				String name = next.getName();
				if (name.startsWith(packagePath)) {
					String remaining = name.substring(packagePath.length());
					if (remaining.startsWith("/")) {
						remaining = remaining.substring(1);
					}
					if (remaining.endsWith(".class")) {
						String fixedName = name.substring(0, name.indexOf('.'))
								.replace('/', '.');
						try {
							Class<?> klass = bundle.loadClass(fixedName);
							if (test.matches(klass)) {
								classes.add(klass);
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}

					}
				}
			}
			jis.close();
		} catch (IOException e) {

		}
	}
}