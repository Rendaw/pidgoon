package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.Node;

/**
 * Implementations for named operatos can be provided to the Parse via a map of lambdas.  This is also used for rules
 * (grammar top level nodes) where the key is used for Reference nodes and specifying the parse root.
 */
public class NamedOperator extends com.zarbosoft.pidgoon.internal.NamedOperator<ClipStore> {

	public NamedOperator(final Object key, final Node root) {
		super(key, root);
	}

	public NamedOperator(final Object key) {
		super(key);
	}
}
