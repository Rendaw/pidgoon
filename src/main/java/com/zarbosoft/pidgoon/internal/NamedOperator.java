package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.Node;

import java.util.Map;

public class NamedOperator<S> extends BaseOperator {

	public final Object key;

	public NamedOperator(final Object key, final Node root) {
		super(root);
		this.key = key;
	}

	public NamedOperator(final Object key) {
		super();
		this.key = key;
	}

	@Override
	protected Store callback(final Store store, final Map<Object, Object> callbacks) {
		final Object found = callbacks.get(key);
		if (found == null)
			return store;
		return ((Callback<S>) found).accept(store);
	}
}
