package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.BranchingStack;
import com.zarbosoft.pidgoon.internal.Grammar;
import com.zarbosoft.pidgoon.internal.Position;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.rendaw.common.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This manages the state of a parse.  This can be used without a Parse object for manually driven parses.
 */
public class ParseContext {

	public ParseContext(
			final Grammar grammar,
			final Map<Object, Object> callbacks,
			final int errorHistoryLimit,
			final int uncertaintyLimit,
			final boolean dumpAmbiguity
	) {
		this.grammar = grammar;
		this.callbacks = callbacks;
		if (callbacks == null)
			this.callbacks = new HashMap<>();
		this.errorHistoryLimit = errorHistoryLimit;
		this.uncertaintyLimit = uncertaintyLimit;
		if (dumpAmbiguity)
			this.ambiguityHistory = new BranchingStack<>(new AmbiguitySample());
	}

	public Grammar grammar;
	public Map<Object, Object> callbacks;
	/**
	 * An error for each branch that failed in the previous step of the parse.
	 */
	public List<Object> errors = new ArrayList<>();

	/**
	 * The error from steps before the previous (controlled by errorHistoryLimit).
	 */
	public List<Pair<Position, List<Object>>> errorHistory = new ArrayList<>();
	public final int errorHistoryLimit;
	public final int uncertaintyLimit;

	/**
	 * This represents the tip node of each branch.
	 */
	public List<State> leaves = new ArrayList<>();
	public Stats stats = new Stats();

	/**
	 * This represents the top value of the stack of branches that matched in the previous step of the parse.
	 */
	public List<Object> results = new ArrayList<>();

	public static class AmbiguitySample {
		public int step;
		public int ambiguity;
		public Position position;
		public int duplicates;

		public AmbiguitySample(final int step, final int ambiguity, final Position position, final int duplicates) {
			this.step = step;
			this.ambiguity = ambiguity;
			this.position = position;
			this.duplicates = duplicates;
		}

		public AmbiguitySample() {
		}
	}

	public BranchingStack<AmbiguitySample> ambiguityHistory;

	public ParseContext(final ParseContext previous) {
		this.grammar = previous.grammar;
		this.callbacks = previous.callbacks;
		this.stats = new Stats(previous.stats);
		stats.totalLeaves += previous.leaves.size();
		stats.maxLeaves = Math.max(stats.maxLeaves, previous.leaves.size());
		stats.steps += 1;
		this.errorHistoryLimit = previous.errorHistoryLimit;
		this.uncertaintyLimit = previous.uncertaintyLimit;
		this.ambiguityHistory = previous.ambiguityHistory;
	}
}
