package com.zarbosoft.pidgoon.events;

import java.util.Map;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Operator;
import com.zarbosoft.pidgoon.source.Store;

public class BakedOperator extends Operator {
	
	private Callback callback;

	public BakedOperator(Node root, Callback callback) {
		super(root);
		this.callback = callback;
	}

	@Override
	protected void callback(Store store, Map<String, Object> callbacks) {
		callback.accept(store);
	}

}
