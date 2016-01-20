package com.zarbosoft.undepurseable.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.GrammarTooAmbiguous;
import com.zarbosoft.undepurseable.InvalidGrammar;
import com.zarbosoft.undepurseable.InvalidStream;

public class GrammarPrivate {
	private Map<String, Node> nodes = new HashMap<>();
	
	public void add(String name, Node node) {
		nodes.put(name, node);
	}
	
	public BranchingStack<Object> parse(String node, InputStream stream) throws IOException {
		final Mutable<Position> position = new Mutable<>(new Position(this, stream));
		final Mutable<Deque<TerminalContext>> leaves = new Mutable<>(new ArrayDeque<>());
		getNode(node).context(position.value, new Store(), new Parent() {
			@Override
			public void error(TerminalContext leaf) {
				position.value.errors.add(leaf);
			}

			@Override
			public void advance(Position position, Store store) {
				position.results.add(store);
			}
			
			@Override
			public void cut(Position position) {
				position.takeLeaves();
				leaves.value.clear();
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
		leaves.value.addAll(position.value.takeLeaves());
		BranchingStack<Object> results = null;
		while (!position.value.isEOF()) {
			//System.out.println(String.format("%s", position));
			while (!leaves.value.isEmpty()) {
				TerminalContext leaf = leaves.value.removeFirst();
				//System.out.println(leaf);
				leaf.parse(position.value);
			}
			//System.out.println("\n");
			leaves.value = new ArrayDeque<>();
			leaves.value.addAll(position.value.getLeaves());
			if (leaves.value.size() >= 1000) throw new GrammarTooAmbiguous(position.value);
			if (!position.value.results.isEmpty())
				results = position.value.results.get(0).stack;
			Position nextPosition = position.value.advance();
			if (!nextPosition.isEOF() && leaves.value.isEmpty()) throw new InvalidStream(position.value);
			position.value = nextPosition;
		}
		if (results == null) {
			return null;
		}
		return results;
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
}