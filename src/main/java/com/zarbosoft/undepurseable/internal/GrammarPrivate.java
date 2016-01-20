package com.zarbosoft.undepurseable.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.GrammarTooAmbiguous;
import com.zarbosoft.undepurseable.InvalidGrammar;
import com.zarbosoft.undepurseable.InvalidStream;
import com.zarbosoft.undepurseable.Stats;

public class GrammarPrivate {
	private Map<String, Node> nodes = new HashMap<>();
	
	public void add(String name, Node node) {
		nodes.put(name, node);
	}

	public Node getNode(String node) {
		if (!nodes.containsKey(node)) throw new InvalidGrammar(String.format("No rule named %s", node));
		return nodes.get(node);
	}

	public String toString() {
		return nodes.entrySet().stream()
			.map(e -> String.format("%s: %s;", e.getKey(), e.getValue()))
			.collect(Collectors.joining("\n"));
	}
	
	public ParseContext prepare(String node, InputStream stream) throws IOException {
		final ParseContext context = new ParseContext(this, stream);
		getNode(node).context(context, new Store(), new Parent() {
			@Override
			public void error(TerminalReader leaf) {
				context.errors.add(leaf);
			}

			@Override
			public void advance(Store store) {
				context.results.add(store.stack);
			}
			
			@Override
			public void cut() {
				context.outLeaves.clear();
				context.leaves.clear();
			}

			@Override
			public String buildPath(String rep) {
				return node + " " + rep;
			}

			@Override
			public long size(Parent stopAt, long start) {
				return start; // Should never be called
			}
		});
		return context;
	}
	
	public void step(ParseContext context) throws IOException {
		step(context, null);
	}
	
	public void step(ParseContext context, Stats stats) throws IOException {
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
			TerminalReader leaf = context.leaves.removeFirst();
			leaf.parse();
		}
		if (context.outLeaves.size() >= 1000) throw new GrammarTooAmbiguous(context);
		Position nextPosition = context.position.advance();
		if (!nextPosition.isEOF() && context.outLeaves.isEmpty()) throw new InvalidStream(context);
		context.position = nextPosition;
		if (!context.results.isEmpty()) context.preferredResult = context.results.get(0);
	}
	
	public BranchingStack<Object> finish(ParseContext context) {
		return context.preferredResult;
	}

	public BranchingStack<Object> parse(String node, InputStream stream) throws IOException {
		return parse(node, stream, null);
	}
	
	public BranchingStack<Object> parse(String node, InputStream stream, Stats stats) throws IOException {
		ParseContext context = prepare(node, stream);
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