package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseContext {
	public ParseContext(final Grammar grammar, final Map<String, Object> callbacks) {
		this.grammar = grammar;
		this.callbacks = callbacks;
		if (callbacks == null) this.callbacks = new HashMap<>();
	}

	public Grammar grammar;
	public Map<String, Object> callbacks;
	public List<Object> errors = new ArrayList<>();
	public List<State> outLeaves = new ArrayList<>();
	public Stats stats = new Stats();
	public Object result = null;

	public ParseContext(final ParseContext previous) {
		this.grammar = previous.grammar;
		this.callbacks = previous.callbacks;
		this.stats = new Stats(previous.stats);
		stats.totalLeaves += previous.outLeaves.size();
		stats.maxLeaves = Math.max(stats.maxLeaves, previous.outLeaves.size());
		stats.steps += 1;
	}
}
