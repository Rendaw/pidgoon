package com.zarbosoft.undepurseable.bytes.internal;

import com.zarbosoft.undepurseable.source.Store;

@FunctionalInterface
public interface Callback {

	public void accept(ClipStore store);
	
	default public void accept(Store store) {
		accept((ClipStore)store);
	}

}
