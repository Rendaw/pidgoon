package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.Node;

import java.util.Map;

public class Operator<S> extends BaseOperator {

	private final Callback<S> callback;

	public Operator(final Node root, final Callback<S> callback) {
		super(root);
		this.callback = callback;
	}

	public Operator(final Callback<S> callback) {
		super();
		this.callback = callback;
	}

	@Override
	protected Store callback(final Store store, final Map<String, Object> callbacks) {
		return callback.accept(store);
	}

}
