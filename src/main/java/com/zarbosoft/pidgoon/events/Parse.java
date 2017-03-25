package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.InvalidStream;
import com.zarbosoft.pidgoon.internal.BaseParse;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.Pair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

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

	public O parse(final Stream<Pair<? extends Event, Object>> data) {
		final Common.Mutable<EventStream<O>> eventStream = new Common.Mutable<>(parse());
		data.forEach(pair -> {
			eventStream.value = eventStream.value.push(pair.first, pair.second.toString());
		});
		return eventStream.value.finish();
	}

	public Stream<O> parseMultiple(final Stream<Pair<? extends Event, Object>> data) {
		class State {
			EventStream<O> stream;
			Deque<Event> events = new ArrayDeque<>();

			private void createStream() {
				stream = parse();
			}

			State() {
				createStream();
			}

			public void handleEvent(final Pair<? extends Event, Object> pair) {
				stream = stream.push(pair.first, pair.second.toString());
			}
		}
		final State state = new State();
		return Common.streamFinality(data.iterator()).map(pair -> {
			state.handleEvent(pair.second);
			if (state.stream.ended()) {
				O result = state.stream.finish();
				state.createStream();
				return result;
			} else if (pair.first) {
				throw new InvalidStream(state.stream.context(), "Premature stream end.");
			} else
				return null;
		}).filter(o -> o != null);
	}

	@Override
	protected Parse<O> split() {
		return new Parse<>(this);
	}

}
