package com.svcdelivery.liquibase.eclipse.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ICheckStateProvider;

public class ProjectStateProvider implements ICheckStateProvider {

	@Override
	public boolean isChecked(Object item) {
		boolean checked = false;
		if (item instanceof IProject) {
			IProject project = (IProject) item;
			try {
				checked = project.hasNature(LiquibaseNature.NATURE);
			} catch (CoreException e) {
				// TODO Log an error;
			}
		}
		return checked;
	}

	@Override
	public boolean isGrayed(Object item) {
		return false;
	}

}
