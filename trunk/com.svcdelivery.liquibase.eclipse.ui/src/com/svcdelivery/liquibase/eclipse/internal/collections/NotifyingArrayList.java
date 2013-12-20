package com.svcdelivery.liquibase.eclipse.internal.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.osgi.framework.ServiceReference;

import com.svcdelivery.liquibase.eclipse.api.LiquibaseService;

public class NotifyingArrayList<E> extends ArrayList<E> implements
		NotifyingList<E> {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	private HashSet<NotifyingListListener<E>> listeners;

	public NotifyingArrayList() {
		super();
		listeners = new HashSet<NotifyingListListener<E>>();
	}

	public NotifyingArrayList(Collection<? extends E> c) {
		super(c);
		listeners = new HashSet<NotifyingListListener<E>>();
	}

	public NotifyingArrayList(int initialCapacity) {
		super(initialCapacity);
		listeners = new HashSet<NotifyingListListener<E>>();
	}

	public NotifyingArrayList(E[] data) {
		this(Arrays.asList(data));
	}

	@Override
	public Object clone() {
		NotifyingArrayList<E> list = (NotifyingArrayList<E>) super.clone();
		list.listeners = (HashSet<NotifyingListListener<E>>) listeners.clone();
		return list;
	}

	@Override
	public E set(final int index, final E element) {
		E e = super.set(index, element);
		ListEvent<E> event = new ListEvent<E>(index, index + 1);
		notify(event);
		return e;
	}

	@Override
	public boolean add(final E e) {
		boolean changed = super.add(e);
		if (changed) {
			ListEvent<E> event = new ListEvent<E>(size() - 1, -1);
			notify(event);
		}
		return changed;
	}

	@Override
	public void add(final int index, final E element) {
		super.add(index, element);
		ListEvent<E> event = new ListEvent<E>(index, -1);
		notify(event);
	}

	@Override
	public E remove(int index) {
		E e = super.remove(index);
		ListEvent<E> event = new ListEvent<E>(index, -1);
		notify(event);
		return e;
	}

	@Override
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i != -1) {
			remove(i);
		}
		return i != -1;
	}

	@Override
	public void clear() {
		super.clear();
		ListEvent<E> event = new ListEvent<E>(0, -1);
		notify(event);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		int end = size();
		boolean changed = super.addAll(c);
		if (changed) {
			ListEvent<E> event = new ListEvent<E>(end, -1);
			notify(event);
		}
		return changed;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean changed = super.addAll(index, c);
		if (changed) {
			ListEvent<E> event = new ListEvent<E>(index, -1);
			notify(event);
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = super.removeAll(c);
		if (changed) {
			ListEvent<E> event = new ListEvent<E>(0, -1);
			notify(event);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = super.retainAll(c);
		if (changed) {
			ListEvent<E> event = new ListEvent<E>(0, -1);
			notify(event);
		}
		return changed;
	}

	@Override
	public void addListener(final NotifyingListListener<E> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(final NotifyingListListener<E> listener) {
		listeners.remove(listener);
	}

	void notify(ListEvent<E> event) {
		for (NotifyingListListener<E> listener : listeners) {
			listener.changed(event);
		}
	}
}
