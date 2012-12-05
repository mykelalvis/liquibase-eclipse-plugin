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
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import liquibase.changelog.RanChangeSet;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * Tree Content provider for data sources and sub elements.
 * 
 * @author nick
 */
public class DataSourceContentProvider implements ILazyTreeContentProvider {
	/**
	 * The tree viewer.
	 */
	private TreeViewer viewer;
	/**
	 * The profile manager.
	 */
	private ProfileManager pm;

	/**
	 * A map of connection profiles to change sets.
	 */
	private TreeMap<IConnectionProfile, List<ChangeSetTreeItem>> changeSets;

	/**
	 * Constructor.
	 */
	public DataSourceContentProvider() {
		Comparator<IConnectionProfile> comparator;
		comparator = new Comparator<IConnectionProfile>() {

			@Override
			public int compare(final IConnectionProfile p1,
					final IConnectionProfile p2) {
				return p1.getName().compareTo(p2.getName());
			}
		};
		changeSets = new TreeMap<IConnectionProfile, List<ChangeSetTreeItem>>(
				comparator);
	}

	/**
	 * @param treeViewer
	 *            The tree viewer.
	 * @param oldInput
	 *            The old input.
	 * @param newInput
	 *            The new ProfileManager input.
	 * @see org.eclipse.jface.viewers.IContentProvider
	 *      #inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public final void inputChanged(final Viewer treeViewer,
			final Object oldInput, final Object newInput) {
		if (treeViewer instanceof TreeViewer) {
			viewer = (TreeViewer) treeViewer;
		}
		if (newInput instanceof ProfileManager) {
			pm = (ProfileManager) newInput;
			pm.addProfileListener(new IProfileListener() {

				@Override
				public void profileDeleted(final IConnectionProfile profile) {
					changeSets.remove(profile);
				}

				@Override
				public void profileChanged(final IConnectionProfile profile) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							viewer.refresh(profile);
						}
					});
				}

				@Override
				public void profileAdded(final IConnectionProfile profile) {
					changeSets.put(profile, null);
				}
			});
			IConnectionProfile[] profiles = pm.getProfiles();
			changeSets.clear();
			for (IConnectionProfile profile : profiles) {
				changeSets.put(profile, null);
			}
		}
	}

	@Override
	public void dispose() {
	}

	/**
	 * @param parent
	 *            The parent element.
	 * @param index
	 *            The element index.
	 * @see org.eclipse.jface.viewers.ILazyTreeContentProvider
	 *      #updateElement(java.lang.Object, int)
	 */
	@Override
	public final void updateElement(final Object parent, final int index) {
		if (pm.equals(parent)) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					Object element = changeSets.keySet().toArray()[index];
					viewer.replace(parent, index, element);
					if (element instanceof IConnectionProfile) {
						viewer.setHasChildren(element, true);
					}
				}
			});
		} else if (parent instanceof IConnectionProfile) {
			final IConnectionProfile profile = (IConnectionProfile) parent;
			final List<ChangeSetTreeItem> ran = changeSets.get(profile);
			if (ran == null) {
				LiquibaseDataSourceScriptLoader loader = new LiquibaseDataSourceScriptLoader() {

					@Override
					public void complete(final List<RanChangeSet> ranChangeSets) {
						final List<ChangeSetTreeItem> items = wrap(profile,
								ranChangeSets);
						changeSets.put(profile, items);
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								if (items.size() == 0) {
									viewer.setChildCount(parent, 0);
								} else {
									ChangeSetTreeItem element = items
											.get(index);
									viewer.replace(parent, index, element);
									if (element instanceof IConnectionProfile) {
										viewer.setHasChildren(element, true);
									}
								}
							}
						});
					}
				};
				loader.loadScripts(profile);
				return;
			}
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					viewer.replace(parent, index, ran.get(index));
				}
			});
		}
	}

	/**
	 * @param element
	 *            The element to update the child count for.
	 * @param currentChildCount
	 *            The current child count.
	 * @see org.eclipse.jface.viewers.ILazyTreeContentProvider
	 *      #updateChildCount(java.lang.Object, int)
	 */
	@Override
	public final void updateChildCount(final Object element,
			final int currentChildCount) {
		int newCount = currentChildCount;
		if (pm.equals(element)) {
			newCount = changeSets.size();
		} else if (element instanceof IConnectionProfile) {
			final IConnectionProfile profile = (IConnectionProfile) element;
			List<ChangeSetTreeItem> ran = changeSets.get(profile);
			if (ran != null) {
				newCount = ran.size();
			}
		}
		if (newCount != currentChildCount) {
			viewer.setChildCount(element, newCount);
		}
	}

	/**
	 * @param profile
	 *            The connection profile.
	 * @param ranChangeSets
	 *            The change sets.
	 * @return a list of change set tree items.
	 */
	private List<ChangeSetTreeItem> wrap(final IConnectionProfile profile,
			final List<RanChangeSet> ranChangeSets) {
		List<ChangeSetTreeItem> items = new ArrayList<ChangeSetTreeItem>();
		for (RanChangeSet ranChangeSet : ranChangeSets) {
			ChangeSetTreeItem item = new ChangeSetTreeItem();
			item.setProfile(profile);
			item.setChangeSet(ranChangeSet);
			items.add(item);
		}
		return items;
	}

	/**
	 * @param element
	 *            The element to get the parent for.
	 * @return The parent element.
	 * @see org.eclipse.jface.viewers.ILazyTreeContentProvider
	 *      #getParent(java.lang.Object)
	 */
	@Override
	public final Object getParent(final Object element) {
		Object parent = null;
		if (pm.equals(element)) {
			parent = null;
		} else if (element instanceof IConnectionProfile) {
			parent = pm;
		} else if (element instanceof ChangeSetTreeItem) {
			ChangeSetTreeItem item = (ChangeSetTreeItem) element;
			parent = item.getProfile();
		}
		return parent;
	}

}
