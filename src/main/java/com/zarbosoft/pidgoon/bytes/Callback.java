package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.source.Store;

@FunctionalInterface
public interface Callback {

	public void accept(ClipStore store);
	
	default public void accept(Store store) {
		accept((ClipStore)store);
	}

}
