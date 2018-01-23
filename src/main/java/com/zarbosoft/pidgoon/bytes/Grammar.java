package com.zarbosoft.pidgoon.bytes;

import com.google.common.primitives.Bytes;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.nio.charset.StandardCharsets;

/**
 * A grammar for parsing byte streams.
 */
public class Grammar extends com.zarbosoft.pidgoon.internal.Grammar {

	/**
	 * Convert a string into a node to match the sequence of equivalent utf-8 bytes.
	 *
	 * @param source
	 * @return
	 */
	public static Sequence stringSequence(final String source) {
		final Sequence out = new Sequence();
		Bytes.asList(source.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public void add(final Object key, final Node node) {
		add(new NamedOperator(key, node));
	}
}
