package com.zarbosoft.pidgoon.bytes;

import com.google.common.primitives.Bytes;
import com.zarbosoft.pidgoon.internal.BaseParse;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Parse<O> extends BaseParse<Parse<O>> {
	private Parse(final Parse<O> other) {
		super(other);
	}

	public Parse() {
	}

	public O parse(final String string) {
		return parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}

	public O parse(final InputStream stream) {
		ClipStore store = new ClipStore();
		if (initialStack != null)
			store = (ClipStore) store.pushStack(initialStack.get());
		return (O) grammar.parse(node, new Position(stream), callbacks, store);
	}

	public static Sequence byteSeq(final List<Byte> list) {
		final Sequence out = new Sequence();
		list.stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public static Sequence stringSeq(final String string) {
		final Sequence out = new Sequence();
		Bytes.asList(string.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	@Override
	protected Parse<O> split() {
		return new Parse<>(this);
	}
}
