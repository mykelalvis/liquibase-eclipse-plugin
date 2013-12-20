package com.svcdelivery.liquibase.eclipse.internal.collections;

import java.util.List;

public interface NotifyingList<E> extends List<E> {
	void addListener(NotifyingListListener<E> listener);
	void removeListener(NotifyingListListener<E> listener);

}
