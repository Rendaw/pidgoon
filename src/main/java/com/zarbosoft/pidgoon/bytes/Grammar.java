package com.zarbosoft.pidgoon.bytes;

import com.google.common.primitives.Bytes;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.nio.charset.StandardCharsets;

public class Grammar extends com.zarbosoft.pidgoon.Grammar {

	public static Sequence stringSequence(final String source) {
		final Sequence out = new Sequence();
		Bytes.asList(source.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public void add(final String name, final Node node) {
		add(new Operator(name, node));
	}
}
