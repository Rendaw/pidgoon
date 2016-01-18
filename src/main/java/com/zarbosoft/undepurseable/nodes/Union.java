package com.zarbosoft.undepurseable.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.internal.BaseParent;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Pair;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Union extends Node {
	List<Node> children = new ArrayList<>();
	
	public Union add(Node child) {
		children.add(child);
		return this;
	}

	@Override
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		Pair.enumerate(children).forEach(p -> {
			Map<String, RefParent> newSeen = new HashMap<>();
			newSeen.putAll(seen);
			p.second.context(startPosition, store.split(), new BaseParent(parent) {
						@Override
						public void advance(Position position, Store store) {
							if (drop) store = store.drop();
							parent.advance(position, store);
						}

						@Override
						public String buildPath(String subpath) {
							return parent.buildPath(String.format("union|%d.%s", p.first, subpath));
						}
					}, newSeen);
		});
	}
	
	public String toString() {
		String out = children.stream()
			.map(c -> c.toString())
			.collect(Collectors.joining(" | "));
		if (drop) return String.format("#(%s)", out);
		return out;
	}
}
