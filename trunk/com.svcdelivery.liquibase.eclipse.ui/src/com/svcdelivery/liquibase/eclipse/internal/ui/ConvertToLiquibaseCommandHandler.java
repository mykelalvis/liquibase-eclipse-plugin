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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Converts the selected projects to Liquibase projects.
 * 
 * @author nick
 * 
 */
public class ConvertToLiquibaseCommandHandler implements IHandler {
	/**
	 * Builder ID.
	 */
	private static final String ID = "liquibase.ide.LiquibaseBuilder";
	/**
	 * Liquibase Plugin Nature.
	 */
	private static final String NATURE = "liquibase.ide";

	@Override
	public void addHandlerListener(final IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public final Object execute(final ExecutionEvent event)
			throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			StructuredSelection structured = (StructuredSelection) selection;
			for (final Object next : structured.toList()) {
				if (next instanceof IProject) {
					final IProject project = (IProject) next;
					try {
						addNature(project);
						addBuilder(project);
					} catch (final CoreException e) {
						throw new ExecutionException(e.getMessage());
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param project
	 *            The project to add the nature to.
	 * @throws CoreException
	 *             If there was a problem adding it.
	 */
	private void addNature(final IProject project) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final String[] natures = desc.getNatureIds();
		for (int i = 0; i < natures.length; ++i) {
			if (natures[i].equals(NATURE)) {
				return;
			}
		}
		final String[] nc = new String[natures.length + 1];
		// Add it before other natures.
		System.arraycopy(natures, 0, nc, 1, natures.length);
		nc[0] = NATURE;
		desc.setNatureIds(nc);
		project.setDescription(desc, null);
	}

	/**
	 * @param project
	 *            The project to add the builder to.
	 * @throws CoreException
	 *             If there was a problem adding it.
	 */
	private void addBuilder(final IProject project) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(ID)) {
				return;
			}
		}
		// add builder to project
		final ICommand command = desc.newCommand();
		command.setBuilderName(ID);
		final ICommand[] nc = new ICommand[commands.length + 1];
		// Add it before other builders.
		System.arraycopy(commands, 0, nc, 1, commands.length);
		nc[0] = command;
		desc.setBuildSpec(nc);
		project.setDescription(desc, null);
	}

	@Override
	public final boolean isEnabled() {
		return true;
	}

	@Override
	public final boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(final IHandlerListener handlerListener) {
	}

}
