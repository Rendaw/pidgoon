package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.source.Store;

import java.util.Map;

public class NamedOperator extends com.zarbosoft.pidgoon.internal.NamedOperator {

	public NamedOperator(final String name, final Node root) {
		super(name, root);
	}

	@Override
	protected com.zarbosoft.pidgoon.source.Store callback(final Store store, final Map<String, Object> callbacks) {
		final Object found = callbacks.get(name);
		if (found == null) return store;
		return ((Callback) found).accept(store);
	}
}
