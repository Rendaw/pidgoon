package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.source.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reference extends Node {

	public class RefParent implements Parent {
		Parent originalParent;
		List<Parent> loopParents = new ArrayList<>();

		public RefParent(final Parent parent) {
			originalParent = parent;
		}

		public void advance(final ParseContext step, final Store store, final Object cause) {
			if (cut) originalParent.cut(step);
			final Store tempStore = store.pop(!drop);
			originalParent.advance(step, tempStore, cause);
			for (final Parent p : loopParents) {
				p.advance(step, tempStore.inject(p.size(this, 1)), cause);
			}
		}

		public String buildPath(final String subpath) {
			return originalParent.buildPath(String.format("<%s> . %s", name, subpath));
		}

		@Override
		public void error(final ParseContext step, final Store store, final Object cause) {
			originalParent.error(step, store, cause);
		}

		@Override
		public long size(final Parent stopAt, final long start) {
			if (stopAt == this) return start;
			return originalParent.size(stopAt, start + 1);
		}

		@Override
		public void cut(final ParseContext step) {
			originalParent.cut(step);
		}
	}

	private Node base = null;
	private final String name;

	public Reference(final String name) {
		super();
		this.name = name;
	}

	private Node get(final ParseContext context) {
		if (base == null) {
			base = context.grammar.getNode(name);
			drop = drop || base.drop;
		}
		return base;
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final Map<String, RefParent> seen,
			final Object cause
	) {
		if (seen.containsKey(name)) {
			seen.get(name).loopParents.add(parent);
			return;
		}
		final RefParent subParent = new RefParent(parent);
		seen.put(name, subParent);
		get(context).context(
				context,
				store.push(),
				subParent, seen,
				cause
		);
	}

	public String toString() {
		if (drop) return "#" + name;
		return name;
	}
}
