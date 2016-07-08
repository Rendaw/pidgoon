package com.zarbosoft.pidgoon.nodes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Set extends Node {
	java.util.Set<Node> children = new HashSet<>();

	public Set add(final Node child) {
		children.add(child);
		return this;
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final Map<String, RefParent> seen,
			final Object cause
	) {
		class SetParent extends BaseParent {
			java.util.Set<Node> remaining;

			public SetParent(final Parent parent, final java.util.Set<Node> remaining) {
				super(parent);
				this.remaining = remaining;
			}

			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				final Store tempStore = store.pop(!drop);
				if (remaining.isEmpty()) {
					if (cut) parent.cut(step);
					parent.advance(step, tempStore, cause);
				} else {
					remaining.forEach(c -> {
						c.context(
								step,
								tempStore.push(),
								new SetParent(parent, Sets.difference(remaining, ImmutableSet.of(c))),
								cause
						);
					});
				}
			}

			@Override
			public String buildPath(final String subpath) {
				return parent.buildPath(String.format("set . %s", subpath));
			}
		}
		children.forEach(c -> {
			final Map<String, RefParent> newSeen = new HashMap<>();
			newSeen.putAll(seen);
			c.context(
					context,
					store.push(),
					new SetParent(parent, Sets.difference(children, ImmutableSet.of(c))),
					newSeen,
					cause
			);
		});
	}

	public String toString() {
		final String out = children.stream()
				.map(c -> {
					if (!c.drop && (c instanceof Union)) return String.format("(%s)", c);
					return c.toString();
				})
				.collect(Collectors.joining(" "));
		if (drop) return String.format("#{%s}", out);
		return String.format("{%s}", out);
	}
}
