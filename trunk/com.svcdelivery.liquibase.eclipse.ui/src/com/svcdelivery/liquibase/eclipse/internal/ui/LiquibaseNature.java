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

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author nick
 */
public class LiquibaseNature implements IProjectNature {

	/**
	 * Liquibase Plugin Nature.
	 */
	public static final String NATURE = "com.svcdelivery.liquibase.eclipse";

	/**
	 * @param project
	 *            The project to add the nature to.
	 * @throws CoreException
	 *             If there was a problem adding it.
	 */
	public static void addNature(final IProject project) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final String[] natures = desc.getNatureIds();
		for (int i = 0; i < natures.length; ++i) {
			if (natures[i].equals(LiquibaseNature.NATURE)) {
				return;
			}
		}
		final String[] nc = new String[natures.length + 1];
		// Add it before other natures.
		System.arraycopy(natures, 0, nc, 1, natures.length);
		nc[0] = LiquibaseNature.NATURE;
		desc.setNatureIds(nc);
		project.setDescription(desc, null);
	}

	/**
	 * @param project
	 *            The project to add the builder to.
	 * @throws CoreException
	 *             If there was a problem adding it.
	 */
	public static void addBuilder(final IProject project) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName()
					.equals(LiquibaseBuilder.BUILDER_ID)) {
				return;
			}
		}
		// add builder to project
		final ICommand command = desc.newCommand();
		command.setBuilderName(LiquibaseBuilder.BUILDER_ID);
		final ICommand[] nc = new ICommand[commands.length + 1];
		// Add it before other builders.
		System.arraycopy(commands, 0, nc, 1, commands.length);
		nc[0] = command;
		desc.setBuildSpec(nc);
		project.setDescription(desc, null);
	}

	/**
	 * @param project
	 *            The project to add the nature to.
	 * @throws CoreException
	 *             If there was a problem adding it.
	 */
	public static void removeNature(final IProject project)
			throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final String[] natures = desc.getNatureIds();
		for (int i = 0; i < natures.length; ++i) {
			if (natures[i].equals(LiquibaseNature.NATURE)) {
				final String[] nc = new String[natures.length - 1];
				System.arraycopy(natures, 0, nc, 0, i);
				System.arraycopy(natures, i + 1, nc, i, natures.length - i - 1);
				desc.setNatureIds(nc);
				project.setDescription(desc, null);
				break;
			}
		}
	}

	/**
	 * @param project
	 *            The project to add the builder to.
	 * @throws CoreException
	 *             If there was a problem adding it.
	 */
	public static void removeBuilder(final IProject project)
			throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName()
					.equals(LiquibaseBuilder.BUILDER_ID)) {
				final ICommand[] nc = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, nc, 0, i);
				System.arraycopy(commands, i + 1, nc, i, commands.length - i
						- 1);
				desc.setBuildSpec(nc);
				project.setDescription(desc, null);
				break;
			}
		}
		ChangeLogCache cache = Activator.getDefault().getChangeLogCache();
		cache.removeFiles(project);
	}

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public final IProject getProject() {
		return null;
	}

	@Override
	public void setProject(final IProject project) {
	}

}
