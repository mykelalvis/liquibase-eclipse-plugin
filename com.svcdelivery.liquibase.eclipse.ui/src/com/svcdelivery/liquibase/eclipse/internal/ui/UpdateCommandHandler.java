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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author nick
 */
public class UpdateCommandHandler implements IHandler {

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
		final List<IFile> files = new ArrayList<IFile>();
		if (selection instanceof StructuredSelection) {
			final StructuredSelection structured = (StructuredSelection) selection;
			for (final Object next : structured.toList()) {
				if (next instanceof IFile) {
					files.add((IFile) next);
				}
			}
		}
		final IWizard targetWizard = new UpdateScriptsWizard(files);
		final Shell shell = HandlerUtil.getActiveShell(event);
		final WizardDialog dialog = new WizardDialog(shell, targetWizard);
		dialog.setPageSize(400, 500);
		dialog.open();
		return null;
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
