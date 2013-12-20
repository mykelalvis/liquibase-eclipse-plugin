package com.svcdelivery.liquibase.eclipse.internal.collections;

/**
 * Listener for collection events.
 * 
 * @author nick
 */
public interface NotifyingListListener<E> {
	void changed(ListEvent<E> event);
}
