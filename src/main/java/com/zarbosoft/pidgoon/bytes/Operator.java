package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.internal.NamedOperator;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.source.Store;

import java.util.Map;

public class Operator extends NamedOperator {

	public Operator(final String name, final Node root) {
		super(name, root);
	}

	@Override
	protected Store callback(final Store store, final Map<String, Object> callbacks) {
		final Object found = callbacks.get(name);
		if (found == null) return store;
		return ((Callback) found).accept(store);
	}

}
