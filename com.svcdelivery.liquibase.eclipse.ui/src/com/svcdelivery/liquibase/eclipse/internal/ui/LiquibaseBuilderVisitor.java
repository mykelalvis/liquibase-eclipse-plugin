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

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

/**
 * @author nick
 */
public class LiquibaseBuilderVisitor implements IResourceDeltaVisitor,
		IResourceVisitor {

	/**
	 * Change log parser factory.
	 */
	private final ChangeLogParserFactory factory;

	/**
	 * Located change logs.
	 */
	private final ChangeLogCache changeLogCache;

	/**
	 * Constructor.
	 */
	public LiquibaseBuilderVisitor() {
		factory = ChangeLogParserFactory.getInstance();
		changeLogCache = Activator.getDefault().getChangeLogCache();
	}

	@Override
	public final boolean visit(final IResourceDelta delta) throws CoreException {
		int kind = delta.getKind();
		IResource resource = delta.getResource();
		if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
			visit(resource);
		} else if (kind == IResourceDelta.REMOVED) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				changeLogCache.remove(file);
			}
		} else {
		}
		return true;
	}

	@Override
	public final boolean visit(final IResource resource) throws CoreException {
		if (resource instanceof IFile) {
			final IFile file = (IFile) resource;
			if (file.exists() && "xml".equals(file.getFileExtension())) {
				final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(
						file.getParent().getLocation().toString());
				try {
					final ChangeLogParser parser = factory.getParser("xml",
							resourceAccessor);
					final ChangeLogParameters params = new ChangeLogParameters();
					final DatabaseChangeLog changeLog = parser
							.parse(file.getName(), params,
									resourceAccessor);
					if (changeLog != null) {
						changeLogCache.add(file, changeLog);
					}
				} catch (final ChangeLogParseException e) {
				} catch (final LiquibaseException e) {
					throw new CoreException(Status.CANCEL_STATUS);
				}

			}
		}
		return true;
	}

}
