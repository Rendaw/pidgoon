package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.BaseParse;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.Pair;

import java.util.stream.Stream;

public class Parse<O> extends BaseParse<Parse<O>> {
	protected Parse(final Parse<O> other) {
		super(other);
	}

	public Parse() {
	}

	/**
	 * Instead of pulling from an input stream, use the returned EventStream to push events to the parse.
	 *
	 * @return
	 */
	public EventStream<O> parse() {
		Store store = new Store();
		if (initialStack != null)
			store = (Store) store.pushStack(initialStack.get());
		return new EventStream<>(grammar, root, callbacks, store, errorHistoryLimit, uncertaintyLimit, dumpAmbiguity);
	}

	/**
	 * Parse by pulling events from the stream.
	 *
	 * @param data
	 * @return
	 */
	public O parse(final Stream<Pair<? extends Event, Object>> data) {
		final Common.Mutable<EventStream<O>> eventStream = new Common.Mutable<>(parse());
		data.forEach(pair -> {
			eventStream.value = eventStream.value.push(pair.first, pair.second.toString());
		});
		return eventStream.value.finish();
	}

	@Override
	protected Parse<O> split() {
		return new Parse<>(this);
	}

}
