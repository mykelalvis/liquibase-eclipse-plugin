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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Holds the change log scripts detected by the builder.
 * 
 * @author nick
 */
public class ChangeLogCache {
	/**
	 * File name for storage file.
	 */
	private static final String FILENAME = "changelogs.properties";

	// @Override
	// public String toString() {
	// StringBuilder sb = new StringBuilder();
	// for (Map.Entry<IFile, DatabaseChangeLog> entry : logs.entrySet()) {
	// IFile file = entry.getKey();
	// sb.append(file.getProjectRelativePath().toPortableString());
	// if (entry.getValue() != null) {
	// sb.append(" (loaded)");
	// }
	// sb.append("\n");
	// }
	// return sb.toString();
	// }

	// private Map<String, IFile> pathToFile;

	private Map<IFile, List<IFile>> imports;

	/**
	 * List of cached change logs.
	 */
//	private Map<IFile, DatabaseChangeLog> logs;

	/**
	 * Listeners for changes to the change log cache.
	 */
	private Set<ChangeLogListener> listeners;

	/**
	 * Change log parser factory.
	 */
//	private final ChangeLogParserFactory factory;

	/**
	 * initialise the change log cache.
	 */
	public ChangeLogCache() {
		listeners = new HashSet<ChangeLogListener>();
//		factory = ChangeLogParserFactory.getInstance();
		load();
	}

	/**
	 * @return The cached change logs.
	 */
	public final Set<IFile> getChangeLogs() {
		return imports.keySet();
	}

	/**
	 * @param changeLogs
	 *            The change logs to cache.
	 */
//	final void setChangeLogs(final Map<IFile, DatabaseChangeLog> changeLogs) {
//		logs = changeLogs;
//	}

	/**
	 * @param file
	 *            the file to remove.
	 */
	public final void remove(final IFile file) {
//		logs.remove(file);
		imports.remove(file);
		for (List<IFile> importList : imports.values()) {
			if (importList != null) {
				importList.remove(file);
			}
		}
		notifyChangeLogRemoved(file);
	}

	/**
	 * @param file
	 *            The file to add.
	 * @param changeLog
	 *            The changelog, or null to lazy load.
	 * @throws CoreException
	 */
	public final void add(final IFile file) throws CoreException {
		if (file.exists() && "xml".equals(file.getFileExtension())) {
			imports.put(file, new ArrayList<IFile>());
//			DatabaseChangeLog changeLog = null;
//			changeLog = loadChangeLog(file);
//			if (changeLog != null) {
//				logs.put(file, changeLog);
				notifyChangeLogUpdated(file);
//			}
		}
	}

//	private DatabaseChangeLog loadChangeLog(final IFile file)
//			throws CoreException {
//		DatabaseChangeLog changeLog = null;
//		final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
//				file.getParent().getLocation().toString()) {
//
//			@Override
//			public InputStream getResourceAsStream(String child)
//					throws IOException {
//				List<IFile> imported = imports.get(file);
//				if (imported == null) {
//					imported = new ArrayList<IFile>();
//					imports.put(file, imported);
//				}
//				IFile childFile = file.getParent().getFile(new Path(child));
//				imported.add(childFile);
//				return super.getResourceAsStream(child);
//			}
//		};
//		try {
//			final ChangeLogParser parser = factory.getParser("xml",
//					resourceAccessor);
//			final ChangeLogParameters params = new ChangeLogParameters();
//			changeLog = parser.parse(file.getName(), params, resourceAccessor);
//		} catch (final ChangeLogParseException e) {
//		} catch (final LiquibaseException e) {
//			throw new CoreException(Status.CANCEL_STATUS);
//		}
//		return changeLog;
//	}

	/**
	 * Load the cached data from storage.
	 */
	public final void load() {
		Properties properties = new Properties();
		FileReader fr = null;
		IPath folder = Activator.getDefault().getStateLocation();
		IPath location = folder.append(FILENAME);
		File file = location.toFile();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
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
		} else {
			// Trigger build of liquibase projects.
			for (IProject project : root.getProjects()) {
				try {
					if (project.hasNature(LiquibaseNature.NATURE)) {
						project.touch(null);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
//		logs = new HashMap<IFile, DatabaseChangeLog>();
		imports = new HashMap<IFile, List<IFile>>();
		Set<String> keys = properties.stringPropertyNames();
		for (String key : keys) {
			IPath path = Path.fromPortableString(key);
			try {
				IFile log = root.getFile(path);
//				logs.put(log, null);
				String importList = properties.getProperty(key);
				if (importList != null && !"-".equals(importList)) {
					String[] importArray = importList.split(",");
					List<IFile> imported = new ArrayList<IFile>();
					for (String importItem : importArray) {
						IPath importPath = Path.fromPortableString(importItem);
						imported.add(root.getFile(importPath));
					}
					imports.put(log, imported);
				}
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
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
		for (Entry<IFile, List<IFile>> entry : imports.entrySet()) {
			IFile log = entry.getKey();
			List<IFile> imported = entry.getValue();
			String logName = toRootRelative(log);
			String imports = toRootRelative(imported);
			properties.put(logName, imports);
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

	private String toRootRelative(List<IFile> imported) {
		String paths = "-";
		if (imported != null && imported.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (IFile file : imported) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(toRootRelative(file));
			}
			paths = sb.toString();
		}
		return paths;
	}

	private String toRootRelative(IFile imported) {
		return imported.getProject().getName() + "/"
				+ imported.getProjectRelativePath().toPortableString();
	}

	/**
	 * @param name
	 *            The file name.
	 * @return The matching workspace file.
	 */
	public final IFile getFile(final String name) {
		IFile file = null;
		if (name.contains("/")) {
			for (Entry<IFile, List<IFile>> entry : imports.entrySet()) {
				IFile key = entry.getKey();
				for (IFile next : entry.getValue()) {
					String nextPath = next.getProjectRelativePath()
							.toPortableString();
					if (nextPath.endsWith(name)) {
						file = key;
						break;
					}
				}
			}

		} else {
			for (IFile next : imports.keySet()) {
				String nextPath = next.getProjectRelativePath()
						.toPortableString();
				if (nextPath.endsWith(name)) {
					file = next;
					break;
				}
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
		Iterator<IFile> files = imports.keySet().iterator();
		while (files.hasNext()) {
			IFile file = files.next();
			if (project.equals(file.getProject())) {
				files.remove();
//				logs.remove(file);
				notifyChangeLogRemoved(file);
			}
		}
		persist();
	}

//	public DatabaseChangeLog getChangeLog(IFile file) {
//		DatabaseChangeLog log = null;
//		if (logs.containsKey(file)) {
//			log = logs.get(file);
//			if (log == null) {
//				try {
//					log = loadChangeLog(file);
//					logs.put(file, log);
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return log;
//	}

	public void clear() {
		List<IFile> removed = new ArrayList<IFile>(imports.keySet());
//		logs.clear();
		imports.clear();
		for (IFile next : removed) {
			notifyChangeLogRemoved(next);
		}
	}
}
