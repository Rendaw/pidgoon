package com.zarbosoft.pidgoon.events;

@FunctionalInterface
public interface Callback {

	public com.zarbosoft.pidgoon.source.Store accept(Store store);

	default public com.zarbosoft.pidgoon.source.Store accept(final com.zarbosoft.pidgoon.source.Store store) {
		return accept((Store) store);
	}

}
