package com.zarbosoft.pidgoon.events;

@FunctionalInterface
public interface Callback {

	public void accept(Store store);
	
	default public void accept(com.zarbosoft.pidgoon.source.Store store) {
		accept((Store)store);
	}

}
