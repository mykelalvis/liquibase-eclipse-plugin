package com.svcdelivery.liquibase.eclipse.internal.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class GenerateScriptCommandHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IResource selected = null;
		if (selection instanceof StructuredSelection) {
			final StructuredSelection structured = (StructuredSelection) selection;
			if (structured.size() == 1) {
				Object first = structured.getFirstElement();
				if (first instanceof IResource) {
					selected = (IResource) first;
				}
			}
		}
		final IWizard targetWizard = new GenerateScriptWizard(selected);
		final Shell shell = HandlerUtil.getActiveShell(event);
		final WizardDialog dialog = new WizardDialog(shell, targetWizard);
		dialog.setPageSize(400, 400);
		dialog.open();
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

}
