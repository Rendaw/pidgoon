package com.zarbosoft.undepurseable.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.InvalidGrammar;
import com.zarbosoft.undepurseable.InvalidStream;

public class GrammarPrivate {
	private Map<String, Node> nodes = new HashMap<>();
	
	public void add(String name, Node node) {
		nodes.put(name, node);
	}
	
	public Deque<Object> parse(String node, InputStream stream) throws IOException {
		Position position = new Position(this, stream);
		getNode(node).context(position, new Store(), new Parent() {
			@Override
			public void error(Position position, String string) {
				position.errors.add(string);
			}

			@Override
			public void advance(Position position, Store store) {
				position.results.add(store);
			}

			@Override
			public String buildPath(String rep) {
				return node + " " + rep;
			}
		});
		List<TerminalContext> leaves = position.leaves;
		position.leaves = new ArrayList<>();
		Deque<Object> results = null;
		while (!position.isEOF()) {
			System.out.println(String.format("%s", position));
			for (TerminalContext leaf : leaves) {
				System.out.println(leaf);
				/*
				System.out.println("Before leaf");
				for (TerminalContext subLeaf : position.leaves)
					System.out.println("\t" + subLeaf);
				*/
				leaf.parse(position);
				/*
				System.out.println("After leaf");
				for (TerminalContext subLeaf : position.leaves)
					System.out.println("\t" + subLeaf);
				System.out.println("");
				*/
			}
			System.out.println("");
			System.out.println("");
			leaves = position.leaves;
			if (leaves.isEmpty()) throw new InvalidStream(position);
			if (!position.results.isEmpty())
				results = position.results.get(0).stack;
			position = position.advance();
		}
		if ((results == null) || results.isEmpty()) {
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