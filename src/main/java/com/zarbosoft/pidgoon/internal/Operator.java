package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.AbortParse;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.nodes.Repeat;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.pidgoon.nodes.Union;
import com.zarbosoft.pidgoon.source.Store;
import org.pcollections.PMap;

import java.util.Map;

public abstract class Operator extends Node {
	private final Node root;

	public Operator(final Node root) {
		super();
		this.root = root;
	}

	protected abstract Store callback(Store store, Map<String, Object> callbacks);

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<String, RefParent> seen,
			final Object cause
	) {
		root.context(context, store.push(), new BaseParent(parent) {

			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				Store tempStore = store;
				if (cut)
					parent.cut(step);
				try {
					tempStore = callback(store, context.callbacks);
				} catch (final AbortParse a) {
					parent.error(step, tempStore, a);
					return;
				}
				parent.advance(step, tempStore.pop(!drop), cause);
			}

			@Override
			public String buildPath(final String subpath) {
				return parent.buildPath(String.format("op . %s", subpath));
			}
		}, seen, cause);
	}

	public String toString() {
		String out = root.toString();
		if (drop && !root.drop) {
			if ((root instanceof Sequence) ||
					(root instanceof Union) ||
					(root instanceof Repeat)) {
				out = String.format("#(%s)", out);
			} else {
				out = String.format("#%s", out);
			}
		}
		return out;
	}
}
