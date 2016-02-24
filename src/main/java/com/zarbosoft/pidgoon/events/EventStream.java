package com.zarbosoft.pidgoon.events;

import java.io.IOException;
import java.util.Map;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.internal.ParseContext;

public class EventStream<O> {
	
	private Position position = new Position();
	private ParseContext context;
	private Grammar grammar;
	
	public EventStream(Grammar grammar, String node, Map<String, Object> callbacks, Store store) throws IOException {
		this.grammar = grammar;
		this.context = grammar.prepare(node, position, callbacks, store);
	}

	public void push(Event e) throws IOException {
		position.event = e;
		grammar.step(context, null);
	}
	
	@SuppressWarnings("unchecked")
	public O finish() {
		return (O) grammar.finish(context);
	}

}
