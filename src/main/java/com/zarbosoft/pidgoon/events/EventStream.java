package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.InvalidStream;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.Grammar;

import java.util.Map;

public class EventStream<O> {

	private long lastDistance = 0;
	private final ParseContext context;
	private final Grammar grammar;

	public EventStream(
			final Grammar grammar,
			final Object root,
			final Map<Object, Object> callbacks,
			final Store store,
			final int errorHistoryLimit,
			final int uncertaintyLimit,
			final boolean dumpAmbiguity
	) {
		this.grammar = grammar;
		this.context = grammar.prepare(root, callbacks, store, errorHistoryLimit, uncertaintyLimit, dumpAmbiguity);
	}

	public EventStream(final ParseContext step, final Grammar grammar) {
		this.context = step;
		this.grammar = grammar;
	}

	public EventStream push(final Event e, final String s) {
		final ParseContext nextStep = grammar.step(context, new Position(e, s, lastDistance++));
		return new EventStream<O>(nextStep, grammar);
	}

	public boolean ended() {
		return context.leaves.isEmpty();
	}

	public O finish() {
		if (context.results.isEmpty())
			throw new InvalidStream(context, "Incomplete stream.");
		return (O) context.results.get(0);
	}

	public ParseContext context() {
		return context;
	}
}
