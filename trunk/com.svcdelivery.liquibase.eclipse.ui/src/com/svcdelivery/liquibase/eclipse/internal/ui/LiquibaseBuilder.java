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

import java.util.List;
import java.util.Map;

import liquibase.changelog.DatabaseChangeLog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author nick
 */
public class LiquibaseBuilder extends IncrementalProjectBuilder {

	@Override
	protected final IProject[] build(final int kind,
			final Map<String, String> args, final IProgressMonitor monitor)
			throws CoreException {
		final IProject project = getProject();
		final LiquibaseBuilderVisitor visitor = new LiquibaseBuilderVisitor();
		if (FULL_BUILD == kind || CLEAN_BUILD == kind) {
			project.accept(visitor);
		} else {
			final IResourceDelta delta = getDelta(project);
			delta.accept(visitor);
		}
		Activator.getDefault().getChangeLogCache().persist();
		return null;
	}

}
