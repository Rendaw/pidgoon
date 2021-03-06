package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Store;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurse via the grammar rule of this name.
 */
public class Reference extends Node {

	public class RefParent implements Parent {
		Parent originalParent;
		List<Parent> loopParents = new ArrayList<>();

		public RefParent(final Parent parent) {
			originalParent = parent;
		}

		public void advance(final ParseContext step, final Store store, final Object cause) {
			final Store tempStore = store.pop(true);
			originalParent.advance(step, tempStore, cause);
			for (final Parent p : loopParents) {
				p.advance(step, tempStore.inject(p.size(this, 1)), cause);
			}
		}

		public String buildPath(final String subpath) {
			return originalParent.buildPath(String.format("<%s> . %s", key, subpath));
		}

		@Override
		public void error(final ParseContext step, final Store store, final Object cause) {
			originalParent.error(step, store, cause);
		}

		@Override
		public long size(final Parent stopAt, final long start) {
			if (stopAt == this)
				return start;
			return originalParent.size(stopAt, start + 1);
		}

		@Override
		public void cut(final ParseContext step) {
			originalParent.cut(step);
		}
	}

	private Node base = null;
	private final Object key;

	public Reference(final Object key) {
		super();
		this.key = key;
	}

	private Node get(final ParseContext context) {
		if (base == null) {
			base = context.grammar.getNode(key);
		}
		return base;
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<Object, RefParent> seen,
			final Object cause
	) {
		if (seen.containsKey(key)) {
			seen.get(key).loopParents.add(parent);
			return;
		}
		final RefParent subParent = new RefParent(parent);
		get(context).context(context, store.push(), subParent, seen.plus(key, subParent), cause);
	}

	public String toString() {
		return key.toString();
	}
}
