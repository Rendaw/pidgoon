package com.zarbosoft.undepurseable;

import com.zarbosoft.undepurseable.internal.Store;

@FunctionalInterface
public interface Callback {

	public void accept(Store store);

}
