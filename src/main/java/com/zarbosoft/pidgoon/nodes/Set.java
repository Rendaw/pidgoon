package com.zarbosoft.pidgoon.nodes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;
import com.zarbosoft.rendaw.common.Pair;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.HashSet;
import java.util.stream.Collectors;

public class Set extends Node {
	java.util.Set<Pair<Node, Boolean>> children = new HashSet<>();

	public Set add(final Node child) {
		children.add(new Pair<>(child, true));
		return this;
	}

	public Set add(final Node child, final boolean required) {
		children.add(new Pair<>(child, required));
		return this;
	}

	private class SetParent extends BaseParent {
		java.util.Set<Pair<Node, Boolean>> remaining;
		Parent parent;

		public SetParent(final Parent parent, final java.util.Set<Pair<Node, Boolean>> remaining) {
			super(parent);
			this.parent = parent;
			this.remaining = remaining;
		}

		@Override
		public void advance(final ParseContext step, final Store store, final Object cause) {
			Set.this.advance(step, store.pop(!drop), parent, HashTreePMap.empty(), cause, remaining);
		}

		@Override
		public String buildPath(final String subpath) {
			return parent.buildPath(String.format("set . %s", subpath));
		}
	}

	private void advance(
			final ParseContext step,
			final Store store,
			final Parent parent,
			final PMap<String, RefParent> seen,
			final Object cause,
			final java.util.Set<Pair<Node, Boolean>> remaining
	) {
		if (remaining.stream().noneMatch(c -> c.second)) {
			if (cut)
				parent.cut(step);
			parent.advance(step, store, cause);
		}
		remaining.forEach(c -> {
			c.first.context(
					step,
					store.push(),
					new SetParent(parent, Sets.difference(remaining, ImmutableSet.of(c))),
					seen,
					cause
			);
		});
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<String, RefParent> seen,
			final Object cause
	) {
		advance(context, store, parent, seen, cause, children);
	}

	public String toString() {
		final String out = children.stream().map(c -> {
			if (!c.first.drop && (c.first instanceof Union))
				return String.format("(%s)", c.first);
			return c.first.toString();
		}).collect(Collectors.joining(" "));
		if (drop)
			return String.format("#{%s}", out);
		return String.format("{%s}", out);
	}
}
