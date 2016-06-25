package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.*;
import com.zarbosoft.pidgoon.source.Position;
import com.zarbosoft.pidgoon.source.Store;

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
			final Position initialPosition,
			final Map<String, Object> callbacks,
			final Store initialStore
	) {
		final ParseContext context = new ParseContext(this, initialPosition, callbacks);
		getNode(node).context(context, initialStore, new Parent() {
			@Override
			public void error(final TerminalReader leaf) {
				context.errors.add(leaf);
			}

			@Override
			public void advance(final Store store) {
				if (store.hasOneResult())
					context.results.add(store.takeResult());
			}

			@Override
			public void cut() {
				context.outLeaves.clear();
				context.leaves.clear();
			}

			@Override
			public String buildPath(final String rep) {
				return node + " " + rep;
			}

			@Override
			public long size(final Parent stopAt, final long start) {
				throw new UnsupportedOperationException();
			}
		});
		return context;
	}

	public void step(final ParseContext context) {
		step(context, null);
	}

	public void step(final ParseContext context, final Stats stats) {
		if (context.position.isEOF()) throw new RuntimeException("Cannot step; end of file reached.");
		if (stats != null) {
			stats.totalLeaves += context.outLeaves.size();
			stats.maxLeaves = Math.max(stats.maxLeaves, context.outLeaves.size());
			stats.steps += 1;
		}
		context.leaves.clear();
		context.leaves.addAll(context.outLeaves);
		context.outLeaves.clear();
		context.results.clear();
		while (!context.leaves.isEmpty()) {
			final TerminalReader leaf = context.leaves.removeFirst();
			leaf.parse();
		}
		if (context.outLeaves.size() >= 1000) throw new GrammarTooAmbiguous(context);
		final Position nextPosition = context.position.advance();
		if (!nextPosition.isEOF() && context.outLeaves.isEmpty()) throw new InvalidStream(context);
		context.position = nextPosition;
		if (!context.results.isEmpty()) context.preferredResult = context.results.get(0);
	}

	public Object finish(final ParseContext context) {
		return context.preferredResult;
	}

	public Object parse(
			final String node,
			final Position initialPosition,
			final Map<String, Object> callbacks,
			final Store initialStore
	) {
		return parse(node, initialPosition, callbacks, initialStore, null);
	}

	public Object parse(
			final String node,
			final Position initialPosition,
			final Map<String, Object> callbacks,
			final Store initialStore,
			final Stats stats
	) {
		final ParseContext context = prepare(node, initialPosition, callbacks, initialStore);
		if (context.position.isEOF()) return null;
		while (!context.position.isEOF()) {
			/*
			System.out.println(String.format(
					"%s\n%s\n\n",
					context.position,
					context.outLeaves.stream()
							.map(l -> l.toString())
							.collect(Collectors.joining("\n"))
			));
			*/
			step(context, stats);
		}
		return finish(context);
	}
}