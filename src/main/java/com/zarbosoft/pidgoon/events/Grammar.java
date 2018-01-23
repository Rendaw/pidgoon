package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.internal.NamedOperator;

public class Grammar extends com.zarbosoft.pidgoon.internal.Grammar {
	public Grammar() {

	}

	public Grammar(final Grammar grammar) {
		nodes.putAll(grammar.nodes);
	}

	/**
	 * Add a rule.
	 *
	 * @param key  The key used to specify the node as the parse root and for References.
	 * @param node
	 */
	public void add(final Object key, final Node node) {
		add(new NamedOperator(key, node));
	}
}
