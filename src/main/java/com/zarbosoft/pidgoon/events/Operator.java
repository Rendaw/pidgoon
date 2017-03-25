package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.internal.Callback;

public class Operator extends com.zarbosoft.pidgoon.internal.Operator<Store> {

	public Operator(
			final Node root, final Callback<Store> callback
	) {
		super(root, callback);
	}

	public Operator(final Callback<Store> callback) {
		super(callback);
	}
}
