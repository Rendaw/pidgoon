package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.source.Store;

@FunctionalInterface
public interface Callback {

	public Store accept(ClipStore store);

	default public Store accept(final Store store) {
		return accept((ClipStore) store);
	}

}
