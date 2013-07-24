/**
 * Copyright 2013 Nick Wilson
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
package com.svcdelivery.liquibase.eclipse.api;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Creates an index jar files that can be searched by package.
 * 
 * @author nick
 * 
 */
public class JarClassIndexer {
	/**
	 * Map of package names to class names.
	 */
	private Map<String, Set<String>> packageToFile;

	/**
	 * Constructor.
	 */
	public JarClassIndexer() {
		packageToFile = new HashMap<String, Set<String>>();
	}

	/**
	 * @param jarUrl
	 *            The jar file to add.
	 */
	public final void addJar(final URL jarUrl) {
		if (jarUrl != null) {
			JarInputStream jis = null;
			try {
				jis = new JarInputStream(jarUrl.openStream());
				JarEntry next;
				while ((next = jis.getNextJarEntry()) != null) {
					String name = next.getName();
					if (name.endsWith(".class")) {
						String fixedName = name.substring(0, name.indexOf('.'))
								.replace('/', '.');
						String packageName = fixedName.substring(0,
								name.lastIndexOf('.'));
						Set<String> classes = packageToFile.get(packageName);
						if (classes == null) {
							classes = new HashSet<String>();
							packageToFile.put(packageName, classes);
						}
						classes.add(fixedName);
					}
				}
			} catch (IOException e) {

			} finally {
				if (jis != null) {
					try {
						jis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @param packageName
	 *            The package to search.
	 * @param searchSubPackages
	 *            true to also include sub-packages.
	 * @return a list of class names.
	 */
	public final Set<String> getClasses(final String packageName,
			final boolean searchSubPackages) {
		Set<String> found;
		if (searchSubPackages) {
			found = new HashSet<String>();
			for (Entry<String, Set<String>> entry : packageToFile.entrySet()) {
				if (entry.getKey().startsWith(packageName)) {
					found.addAll(entry.getValue());
				}
			}
		} else {
			found = packageToFile.get(packageName);
			if (found == null) {
				found = new HashSet<String>();
			}
		}
		return found;
	}
}
