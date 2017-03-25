package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.internal.Callback;

public class Operator extends com.zarbosoft.pidgoon.internal.Operator<ClipStore> {

	public Operator(
			final Node root, final Callback<ClipStore> callback
	) {
		super(root, callback);
	}

	public Operator(final Callback<ClipStore> callback) {
		super(callback);
	}
}
