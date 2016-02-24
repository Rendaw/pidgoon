package com.zarbosoft.pidgoon.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.zarbosoft.pidgoon.internal.BaseParse;
import com.zarbosoft.pidgoon.nodes.Sequence;

public class Parse<O> extends BaseParse<O> {
	private Parse(Parse<O> other) {
		super(other);
	}

	public Parse() {}

	@Override
	@SuppressWarnings("unchecked")
	public O parse(InputStream stream) throws IOException {
		ClipStore store = new ClipStore();
		if (initialStack != null)
			store.pushStack(initialStack.get());
		return (O) grammar.parse(node, new Position(stream), callbacks, store);
	}

	public static Sequence byteSeq(List<Byte> list) {
		Sequence out = new Sequence();
		list.stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public static Sequence stringSeq(String string) {
		Sequence out = new Sequence();
		Bytes.asList(string.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	@Override
	protected BaseParse<O> split() {
		return new Parse<O>(this);
	}

}
