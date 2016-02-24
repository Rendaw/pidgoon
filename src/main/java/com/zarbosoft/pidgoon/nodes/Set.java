package com.zarbosoft.pidgoon.nodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

public class Set extends Node {
	java.util.Set<Node> children = new HashSet<>();
	
	public Set add(Node child) {
		children.add(child);
		return this;
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		class SetParent extends BaseParent {
			java.util.Set<Node> remaining;
			
			public SetParent(Parent parent, java.util.Set<Node> remaining) {
				super(parent);
				this.remaining = remaining;
			}

			@Override
			public void advance(Store store) {
				store.pop(!drop);
				if (remaining.isEmpty()) {
					if (cut) parent.cut();
					parent.advance(store);
				} else {
					remaining.forEach(c -> {
						c.context(context, store.push(), new SetParent(parent, Sets.difference(remaining, ImmutableSet.of(c))));
					});
				}
			}

			@Override
			public String buildPath(String subpath) {
				return parent.buildPath(String.format("set . %s", subpath));
			}
		}
		children.forEach(c -> {
			Map<String, RefParent> newSeen = new HashMap<>();
			newSeen.putAll(seen);
			c.context(context, store.push(), new SetParent(parent, Sets.difference(children, ImmutableSet.of(c))), newSeen);
		});
	}
	
	public String toString() {
		String out = children.stream()
			.map(c -> {
				if (!c.drop && (c instanceof Union)) return String.format("(%s)", c);
				return c.toString();
			})
			.collect(Collectors.joining(" "));
		if (drop) return String.format("#{%s}", out);
		return out;
	}
}
