package com.zarbosoft.pidgoon.bytes;

import java.util.Map;

import com.zarbosoft.pidgoon.internal.NamedOperator;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.source.Store;

public class Operator extends NamedOperator {

	public Operator(String name, Node root) {
		super(name, root);
	}

	@Override
	protected void callback(Store store, Map<String, Object> callbacks) {
		Object found = callbacks.get(name);
		if (found == null) return;
		((Callback)found).accept(store);
	}

}
