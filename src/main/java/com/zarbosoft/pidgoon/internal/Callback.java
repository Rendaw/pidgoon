package com.zarbosoft.pidgoon.internal;

@FunctionalInterface
public interface Callback<S> {

	public Store acceptInternal(S store);

	default public Store accept(final Store store) {
		return acceptInternal((S) store);
	}

}
