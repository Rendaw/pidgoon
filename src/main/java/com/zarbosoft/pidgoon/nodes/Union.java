package com.zarbosoft.pidgoon.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Pair;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

public class Union extends Node {
	List<Node> children = new ArrayList<>();
	
	public Union add(Node child) {
		children.add(child);
		return this;
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		Pair.enumerate(children).forEach(p -> {
			Map<String, RefParent> newSeen = new HashMap<>();
			newSeen.putAll(seen);
			p.second.context(context, store.split().pushData(), new BaseParent(parent) {
				@Override
				public void advance(Store store) {
					if (cut) parent.cut();
					store.popData(!drop);
					parent.advance(store);
				}

				@Override
				public String buildPath(String subpath) {
					return parent.buildPath(String.format("union|%d . %s", p.first + 1, subpath));
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
