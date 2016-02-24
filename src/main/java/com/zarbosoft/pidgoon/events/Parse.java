package com.zarbosoft.pidgoon.events;

import java.io.IOException;
import java.io.InputStream;

import com.zarbosoft.pidgoon.internal.BaseParse;

public class Parse<O> extends BaseParse<Parse<O>> {
	private Parse(Parse<O> other) {
		super(other);
	}

	public Parse() {}

	@SuppressWarnings("unchecked")
	public EventStream<O> parse(InputStream stream) throws IOException {
		Store store = new Store();
		if (initialStack != null)
			store.pushStack(initialStack.get());
		return new EventStream<O>(grammar, node, callbacks, store);
	}

	@Override
	protected Parse<O> split() {
		return new Parse<O>(this);
	}

}
