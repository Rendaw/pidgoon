package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.Node;

public class Grammar extends com.zarbosoft.pidgoon.Grammar {
	public void add(String name, Node node) {
		add(new NamedOperator(name, node));
	}
}
