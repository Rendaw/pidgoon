package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.internal.NamedOperator;

public class Grammar extends com.zarbosoft.pidgoon.internal.Grammar {
	public void add(final String name, final Node node) {
		add(new NamedOperator(name, node));
	}
}
