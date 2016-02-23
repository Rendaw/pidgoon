package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.internal.Node;

public class Grammar extends com.zarbosoft.pidgoon.Grammar {
	public void add(String name, Node node) {
		add(new Operator(name, node));
	}
}
