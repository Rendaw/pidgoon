package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.*;
import com.zarbosoft.pidgoon.source.Position;
import com.zarbosoft.pidgoon.source.Store;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Grammar {
	private final Map<String, NamedOperator> nodes = new HashMap<>();

	public void add(final NamedOperator node) {
		nodes.put(node.name, node);
	}

	public Node getNode(final String node) {
		if (!nodes.containsKey(node)) throw new InvalidGrammar(String.format("No rule named %s", node));
		return nodes.get(node);
	}

	public String toString() {
		return nodes.entrySet().stream()
				.map(e -> String.format("%s: %s;", e.getKey(), e.getValue()))
				.collect(Collectors.joining("\n"));
	}

	public ParseContext prepare(
			final String node,
			final Map<String, Object> callbacks,
			final Store initialStore
	) {
		final ParseContext context = new ParseContext(this, callbacks);
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
				step.outLeaves.clear();
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

	public ParseContext step(final ParseContext context, final Position position) {
		if (position.isEOF()) throw new RuntimeException("Cannot step; end of file reached.");
		final int leavesBefore = context.outLeaves.size();
		final ParseContext nextStep = new ParseContext(context);
		final Deque<State> leaves = new ArrayDeque<>();
		leaves.addAll(context.outLeaves);
		while (!leaves.isEmpty()) {
			final State leaf = leaves.removeFirst();
			leaf.parse(nextStep, position);
		}
		if (nextStep.outLeaves.size() >= 1000) throw new GrammarTooAmbiguous(nextStep, position);
		if (nextStep.outLeaves.isEmpty() && nextStep.errors.size() == leavesBefore)
			throw new InvalidStream(nextStep, position);
		if (nextStep.result == null) nextStep.result = context.result;
		return nextStep;
	}

	public Object finish(final ParseContext context) {
		return context.result;
	}

	public Object parse(
			final String node,
			final Position initialPosition,
			final Map<String, Object> callbacks,
			final Store initialStore
	) {
		Position position = initialPosition;
		ParseContext context = prepare(node, callbacks, initialStore);
		if (position.isEOF()) return null;
		while (!position.isEOF()) {
			System.out.println(String.format(
					"%d\n%s\n%s\n\n",
					context.hashCode(),
					position,
					context.outLeaves.stream()
							.map(l -> l.toString())
							.collect(Collectors.joining("\n"))
			));
			context = step(context, position);
			final Position previousPosition = position;
			position = position.advance();
		}
		return finish(context);
	}
}