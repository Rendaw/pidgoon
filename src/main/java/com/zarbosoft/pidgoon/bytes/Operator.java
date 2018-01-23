package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.internal.Callback;

/**
 * Runs code when the child node matches.
 */
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
