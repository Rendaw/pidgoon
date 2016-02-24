package com.zarbosoft.pidgoon.events;

import java.io.IOException;
import java.io.InputStream;

import com.zarbosoft.pidgoon.bytes.Position;
import com.zarbosoft.pidgoon.internal.BaseParse;

public class Parse<O> extends BaseParse<O> {
	private Parse(Parse<O> other) {
		super(other);
	}

	public Parse() {}

	@Override
	@SuppressWarnings("unchecked")
	public O parse(InputStream stream) throws IOException {
		Store store = new Store();
		if (initialStack != null)
			store.pushStack(initialStack.get());
		return (O) grammar.parse(node, new Position(stream), callbacks, store);
	}

	@Override
	protected BaseParse<O> split() {
		return new Parse<O>(this);
	}

}
