package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.BaseParse;

public class Parse<O> extends BaseParse<Parse<O>> {
	protected Parse(Parse<O> other) {
		super(other);
	}

	public Parse() {}

	@SuppressWarnings("unchecked")
	public EventStream<O> parse() {
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
