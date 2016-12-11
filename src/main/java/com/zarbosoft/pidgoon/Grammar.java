package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.*;
import com.zarbosoft.pidgoon.source.Position;
import com.zarbosoft.pidgoon.source.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Grammar {
	private final Map<String, NamedOperator> nodes = new HashMap<>();

	public void add(final NamedOperator node) {
		if (nodes.containsKey(node.name))
			throw new AssertionError(String.format("Node with name [%s] already exists.", node.name));
		nodes.put(node.name, node);
	}

	public Node getNode(final String node) {
		if (!nodes.containsKey(node))
			throw new InvalidGrammar(String.format("No rule named %s", node));
		return nodes.get(node);
	}

	public String toString() {
		return nodes
				.entrySet()
				.stream()
				.map(e -> String.format("%s: %s;", e.getKey(), e.getValue()))
				.collect(Collectors.joining("\n"));
	}

	public ParseContext prepare(
			final String node,
			final Map<String, Object> callbacks,
			final Store initialStore,
			final int errorHistoryLimit,
			final int uncertaintyLimit
	) {
		final ParseContext context = new ParseContext(this, callbacks, errorHistoryLimit, uncertaintyLimit);
		getNode(node).context(context, initialStore, new Parent() {
			@Override
			public void error(final ParseContext step, final Store store, final Object cause) {
				step.errors.add(cause);
			}

			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				if (store.hasOneResult() && step.result == null)
					step.result = store.stackTop();
			}

			@Override
			public void cut(final ParseContext step) {
				step.leaves.clear();
			}

			@Override
			public String buildPath(final String rep) {
				return node + " " + rep;
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
		if (nextStep.leaves.size() > nextStep.uncertaintyLimit)
			throw new GrammarTooUncertain(nextStep, position);
		if (nextStep.leaves.isEmpty() && nextStep.errors.size() == currentStep.leaves.size())
			throw new InvalidStream(nextStep);
		if (nextStep.result == null)
			nextStep.result = currentStep.result;
		return nextStep;
	}

	public Object finish(final ParseContext context) {
		return context.result;
	}

	public Object parse(
			final String node,
			final Position initialPosition,
			final Map<String, Object> callbacks,
			final Store initialStore,
			final int errorHistoryLimit,
			final int uncertaintyLimit
	) {
		Position position = initialPosition;
		ParseContext context = prepare(node, callbacks, initialStore, errorHistoryLimit, uncertaintyLimit);
		if (position.isEOF())
			return null;
		while (!position.isEOF()) {
			/*
			System.out.println(String.format(
					"%d\n%s\n%s\n",
					context.hashCode(),
					position,
					context.leaves.stream().map(l -> l.toString()).collect(Collectors.joining("\n"))
			));
			*/
			context = step(context, position);
			position = position.advance();
		}
		return finish(context);
	}
}