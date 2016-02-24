package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.BaseStore;
import com.zarbosoft.pidgoon.source.Position;

public class Store extends BaseStore {

	public Store(Store store) {
		super(store);
	}

	public Store() {
	}

	@Override
	public com.zarbosoft.pidgoon.source.Store split() {
		return new Store(this);
	}

	@Override
	public void pop(boolean combine) {}

	@Override
	public com.zarbosoft.pidgoon.source.Store push() { return this; }

	@Override
	public void inject(long size) {}

	@Override
	public void record(Position position) {}

}
