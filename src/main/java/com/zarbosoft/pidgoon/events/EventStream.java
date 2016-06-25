package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.internal.ParseContext;

import java.util.Map;

public class EventStream<O> {

	private final Position position = new Position();
	private final ParseContext context;
	private final Grammar grammar;

	public EventStream(
			final Grammar grammar,
			final String node,
			final Map<String, Object> callbacks,
			final Store store
	) {
		this.grammar = grammar;
		this.context = grammar.prepare(node, position, callbacks, store);
	}

	public void push(final Event e, final String s) {
		position.event = e;
		position.at = s;
		grammar.step(context, null);
	}

	public O finish() {
		return (O) grammar.finish(context);
	}

}
