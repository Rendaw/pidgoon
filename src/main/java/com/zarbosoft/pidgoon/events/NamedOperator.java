package com.zarbosoft.pidgoon.events;

import java.util.Map;

import com.zarbosoft.pidgoon.internal.Node;

public class NamedOperator extends com.zarbosoft.pidgoon.internal.NamedOperator {

	public NamedOperator(String name, Node root) {
		super(name, root);
	}

	@Override
	protected void callback(com.zarbosoft.pidgoon.source.Store store, Map<String, Object> callbacks) {
		Object found = callbacks.get(name);
		if (found == null) return;
		((Callback)found).accept(store);
	}
}
