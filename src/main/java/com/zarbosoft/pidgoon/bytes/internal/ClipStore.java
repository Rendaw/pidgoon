package com.zarbosoft.pidgoon.bytes.internal;

import com.zarbosoft.pidgoon.internal.BaseStore;
import com.zarbosoft.pidgoon.source.Store;

public class ClipStore extends BaseStore {

	public ClipStore() {
		super();
	}

	private ClipStore(ClipStore base) {
		super(base);
	}

	@Override
	protected Object createEmptyData() {
		return new Clip();
	}

	@Override
	protected Object combineData(Object first, Object second) {
		return ((Clip)first).cat((Clip)second);
	}

	@Override
	public Store split() {
		return new ClipStore(this);
	}

	public Clip topData() {
		return (Clip)data.top();
	}

}
