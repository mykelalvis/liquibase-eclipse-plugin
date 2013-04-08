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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author nick
 */
public class LiquibaseBuilderVisitor implements IResourceDeltaVisitor,
		IResourceVisitor {

	/**
	 * Located change logs.
	 */
	private final ChangeLogCache changeLogCache;

	/**
	 * Constructor.
	 */
	public LiquibaseBuilderVisitor() {
		changeLogCache = Activator.getDefault().getChangeLogCache();
	}

	@Override
	public final boolean visit(final IResourceDelta delta) throws CoreException {
		int kind = delta.getKind();
		IResource resource = delta.getResource();
		if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
			resource.accept(this);
		} else if (kind == IResourceDelta.REMOVED) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				changeLogCache.remove(file);
			}
		} else {
		}
		return false;
	}

	@Override
	public final boolean visit(final IResource resource) throws CoreException {
		if (resource instanceof IFile) {
			final IFile file = (IFile) resource;
			changeLogCache.add(file);
		}
		return true;
	}

}
