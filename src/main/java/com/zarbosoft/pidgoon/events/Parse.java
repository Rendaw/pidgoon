package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.BaseParse;

public class Parse<O> extends BaseParse<Parse<O>> {
	protected Parse(final Parse<O> other) {
		super(other);
	}

	public Parse() {
	}

	public EventStream<O> parse() {
		Store store = new Store();
		if (initialStack != null)
			store = (Store) store.pushStack(initialStack.get());
		return new EventStream<>(grammar, node, callbacks, store, errorHistoryLimit, uncertaintyLimit, dumpAmbiguity);
	}

	@Override
	protected Parse<O> split() {
		return new Parse<>(this);
	}

}
