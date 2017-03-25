package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.Node;

import java.util.Map;

public class NamedOperator<S> extends BaseOperator {

	public final String name;

	public NamedOperator(final String name, final Node root) {
		super(root);
		this.name = name;
	}

	public NamedOperator(final String name) {
		super();
		this.name = name;
	}

	@Override
	protected Store callback(final Store store, final Map<String, Object> callbacks) {
		final Object found = callbacks.get(name);
		if (found == null)
			return store;
		return ((Callback<S>) found).accept(store);
	}
}
