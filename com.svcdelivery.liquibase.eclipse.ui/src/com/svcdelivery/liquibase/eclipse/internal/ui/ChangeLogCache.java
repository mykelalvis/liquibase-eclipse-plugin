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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import liquibase.changelog.DatabaseChangeLog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Holds the change log scripts detected by the builder.
 * 
 * TODO: Hold change logs as a tree to show imports.
 * 
 * @author nick
 */
public class ChangeLogCache {
	/**
	 * File name for storage file.
	 */
	private static final String FILENAME = "changelogs.csv";

	/**
	 * List of cached change logs.
	 */
	private Map<IFile, DatabaseChangeLog> logs;

	/**
	 * Listeners for changes to the change log cache.
	 */
	private Set<ChangeLogListener> listeners;

	/**
	 * initialise the change log cache.
	 */
	public ChangeLogCache() {
		listeners = new HashSet<ChangeLogListener>();
		load();
	}

	/**
	 * @return The cached change logs.
	 */
	public final Map<IFile, DatabaseChangeLog> getChangeLogs() {
		return logs;
	}

	/**
	 * @param changeLogs
	 *            The change logs to cache.
	 */
	final void setChangeLogs(final Map<IFile, DatabaseChangeLog> changeLogs) {
		logs = changeLogs;
	}

	/**
	 * @param file
	 *            the file to remove.
	 */
	public final void remove(final IFile file) {
		logs.remove(file);
		notifyChangeLogRemoved(file);
	}

	/**
	 * @param file
	 *            The file to add.
	 * @param changeLog
	 *            The changelog, or null to lazy load.
	 */
	public final void add(final IFile file, final DatabaseChangeLog changeLog) {
		logs.put(file, changeLog);
		notifyChangeLogUpdated(file);
	}

	/**
	 * Load the cached data from storage.
	 */
	public final void load() {
		Properties properties = new Properties();
		FileReader fr = null;
		IPath folder = Activator.getDefault().getStateLocation();
		IPath location = folder.append(FILENAME);
		File file = location.toFile();
		if (file.exists()) {
			try {
				fr = new FileReader(file);
				properties.load(fr);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		logs = new HashMap<IFile, DatabaseChangeLog>();
		Set<String> keys = properties.stringPropertyNames();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (String key : keys) {
			IPath path = Path.fromOSString(key);
			IFile log = root.getFile(path);
			logs.put(log, null);
		}
	}

	/**
	 * Persist the list of changelog files to storage.
	 */
	public final void persist() {
		IPath folder = Activator.getDefault().getStateLocation();
		IPath location = folder.append(FILENAME);
		File file = location.toFile();
		file.delete();
		Properties properties = new Properties();
		for (IFile log : logs.keySet()) {
			String logName = log.getProject().getName() + File.separator
					+ log.getProjectRelativePath().toOSString();
			properties.put(logName, "-");
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			properties.store(fw, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param name
	 *            The file name.
	 * @return The matching workspace file.
	 */
	public final IFile getFile(final String name) {
		IFile file = null;
		for (IFile next : logs.keySet()) {
			if (next.getName().equals(name)) {
				file = next;
				break;
			}
		}
		return file;
	}

	/**
	 * @param listener
	 *            The listener to add.
	 */
	public void addChangeLogListener(ChangeLogListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeChangeLogListener(ChangeLogListener listener) {
		listeners.remove(listener);
	}

	private void notifyChangeLogUpdated(IFile file) {
		for (ChangeLogListener listener : listeners) {
			listener.changeLogUpdated(file);
		}
	}

	private void notifyChangeLogRemoved(IFile file) {
		for (ChangeLogListener listener : listeners) {
			listener.changeLogRemoved(file);
		}
	}

	public void removeFiles(IProject project) {
		Iterator<IFile> files = logs.keySet().iterator();
		while (files.hasNext()) {
			IFile file = files.next();
			if (project.equals(file.getProject())) {
				files.remove();
				notifyChangeLogRemoved(file);
			}
		}
		persist();
	}

	public DatabaseChangeLog getChangeLog(IFile file) {
		return logs.get(file);
	}
}
