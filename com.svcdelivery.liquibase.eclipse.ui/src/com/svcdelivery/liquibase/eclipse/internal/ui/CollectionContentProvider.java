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

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.svcdelivery.liquibase.eclipse.internal.collections.ListEvent;
import com.svcdelivery.liquibase.eclipse.internal.collections.NotifyingList;
import com.svcdelivery.liquibase.eclipse.internal.collections.NotifyingListListener;

/**
 * @author nick
 */
public class CollectionContentProvider<E> implements
		IStructuredContentProvider, NotifyingListListener<E> {
	private Viewer viewer;

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		if (oldInput instanceof NotifyingList) {
			NotifyingList<E> ol = (NotifyingList<E>) oldInput;
			ol.removeListener(this);
		}
		this.viewer = viewer;
		if (newInput instanceof NotifyingList) {
			NotifyingList<E> nl = (NotifyingList<E>) newInput;
			nl.addListener(this);
		}
	}

	@Override
	public final Object[] getElements(final Object input) {
		Object[] items = new Object[0];
		if (input instanceof Collection) {
			final Collection<?> c = (Collection<?>) input;
			items = c.toArray();
		}
		return items;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void changed(ListEvent<E> event) {
		if (viewer != null) {
			viewer.refresh();
		}
	}

}
