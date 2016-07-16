package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.Stats;
import com.zarbosoft.pidgoon.source.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseContext {

	public ParseContext(
			final Grammar grammar,
			final Map<String, Object> callbacks,
			final int errorHistoryLimit,
			final int uncertaintyLimit
	) {
		this.grammar = grammar;
		this.callbacks = callbacks;
		if (callbacks == null)
			this.callbacks = new HashMap<>();
		this.errorHistoryLimit = errorHistoryLimit;
		this.uncertaintyLimit = uncertaintyLimit;
	}

	public Grammar grammar;
	public Map<String, Object> callbacks;
	public List<Object> errors = new ArrayList<>();
	public List<Pair<Position, List<Object>>> errorHistory = new ArrayList<>();
	public final int errorHistoryLimit;
	public final int uncertaintyLimit;
	public List<State> leaves = new ArrayList<>();
	public Stats stats = new Stats();
	public Object result = null;

	public ParseContext(final ParseContext previous) {
		this.grammar = previous.grammar;
		this.callbacks = previous.callbacks;
		this.stats = new Stats(previous.stats);
		stats.totalLeaves += previous.leaves.size();
		stats.maxLeaves = Math.max(stats.maxLeaves, previous.leaves.size());
		stats.steps += 1;
		this.errorHistoryLimit = previous.errorHistoryLimit;
		this.uncertaintyLimit = previous.uncertaintyLimit;
	}
}
