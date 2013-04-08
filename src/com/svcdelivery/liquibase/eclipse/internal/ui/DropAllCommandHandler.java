package com.svcdelivery.liquibase.eclipse.internal.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class DropAllCommandHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IConnectionProfile selected = null;
		if (selection instanceof StructuredSelection) {
			final StructuredSelection structured = (StructuredSelection) selection;
			if (structured.size() == 1) {
				Object next = structured.getFirstElement();
				if (next instanceof IConnectionProfile) {
					selected = (IConnectionProfile) next;
				}
			}
		}
		final IWizard targetWizard = new DropAllWizard(selected);
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

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

}
