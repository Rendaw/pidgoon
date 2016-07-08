package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Operator;
import com.zarbosoft.pidgoon.source.Store;

import java.util.Map;

public class BakedOperator extends Operator {

	private final Callback callback;

	public BakedOperator(final Node root, final Callback callback) {
		super(root);
		this.callback = callback;
	}

	@Override
	protected Store callback(final Store store, final Map<String, Object> callbacks) {
		return callback.accept(store);
	}

}
