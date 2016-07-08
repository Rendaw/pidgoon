package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.source.Store;

public abstract class BaseParent implements Parent {

	private final Parent parent;

	public BaseParent(final Parent parent) {
		super();
		this.parent = parent;
	}

	public void error(final ParseContext step, final Store store, final Object cause) {
		parent.error(step, store, cause);
	}

	public long size(final Parent stopAt, final long start) {
		return parent.size(stopAt, start + 1);
	}

	public void cut(final ParseContext step) {
		parent.cut(step);
	}
}
