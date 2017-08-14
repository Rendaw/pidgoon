package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.*;
import com.zarbosoft.rendaw.common.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
	protected final Map<Object, NamedOperator> nodes = new HashMap<>();

	public void add(final NamedOperator node) {
		if (nodes.containsKey(node.key))
			throw new AssertionError(String.format("Node with name [%s] already exists.", node.key));
		nodes.put(node.key, node);
	}

	public Node getNode(final Object key) {
		if (!nodes.containsKey(key))
			throw new InvalidGrammar(String.format("No rule named %s", key));
		return nodes.get(key);
	}

	public String toString() {
		return nodes
				.entrySet()
				.stream()
				.map(e -> String.format("%s: %s;", e.getKey(), e.getValue()))
				.collect(Collectors.joining("\n"));
	}

	public ParseContext prepare(
			final Object root,
			final Map<Object, Object> callbacks,
			final Store initialStore,
			final int errorHistoryLimit,
			final int uncertaintyLimit,
			final boolean dumpAmbiguity
	) {
		final ParseContext context =
				new ParseContext(this, callbacks, errorHistoryLimit, uncertaintyLimit, dumpAmbiguity);
		getNode(root).context(context, initialStore, new Parent() {
			@Override
			public void error(final ParseContext step, final Store store, final Object cause) {
				step.errors.add(cause);
			}

			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				if (store.hasOneResult())
					step.results.add(store.stackTop());
			}

			@Override
			public void cut(final ParseContext step) {
				step.leaves.clear();
			}

			@Override
			public String buildPath(final String rep) {
				return root + " " + rep;
			}

			@Override
			public long size(final Parent stopAt, final long start) {
				throw new UnsupportedOperationException();
			}
		}, "<SOF>");
		return context;
	}

	public ParseContext step(final ParseContext currentStep, final Position position) {
		if (position.isEOF())
			throw new RuntimeException("Cannot step; end of file reached.");
		if (currentStep.leaves.isEmpty())
			throw new InvalidStream(currentStep,
					String.format("Reached end of grammar.\nCurrent position: %s", position)
			);
		final ParseContext nextStep = new ParseContext(currentStep);
		for (final State leaf : currentStep.leaves)
			leaf.parse(nextStep, position);
		if (currentStep.errorHistoryLimit > 0) {
			if (nextStep.errors.isEmpty()) {
				nextStep.errorHistory = currentStep.errorHistory;
				if (nextStep.errorHistory == null)
					nextStep.errorHistory = new ArrayList<>();
			} else {
				nextStep.errorHistory = new ArrayList<>();
				nextStep.errorHistory.add(new Pair<>(position, nextStep.errors));
				currentStep.errorHistory.stream().allMatch(s -> {
					if (nextStep.errorHistory.size() >= currentStep.errorHistoryLimit)
						return false;
					nextStep.errorHistory.add(s);
					return true;
				});
			}
		}
		if (nextStep.ambiguityHistory != null) {
			int dupeCount = 0;
			final Set<String> unique = new HashSet<>();
			for (final State leaf : nextStep.leaves) {
				if (unique.contains(leaf.toString())) {
					dupeCount += 1;
				} else {
					unique.add(leaf.toString());
				}
			}
			nextStep.ambiguityHistory =
					nextStep.ambiguityHistory.push(new ParseContext.AmbiguitySample(nextStep.ambiguityHistory.top().step +
							1, nextStep.leaves.size(), position, dupeCount));
		}
		if (nextStep.leaves.size() > nextStep.uncertaintyLimit)
			throw new GrammarTooUncertain(nextStep, position);
		if (nextStep.leaves.isEmpty() && nextStep.errors.size() == currentStep.leaves.size())
			throw new InvalidStream(nextStep, position);
			/*
		if (nextStep.ambiguityHistory != null)
			System.out.println(String.format(
					"\n%d ==============\n%d\n%s\n%s\n",
					nextStep.ambiguityHistory.top().step,
					nextStep.hashCode(),
					position,
					nextStep.leaves.stream().map(l -> l.toString()).collect(Collectors.joining("\n"))
			));
			*/
		return nextStep;
	}
}