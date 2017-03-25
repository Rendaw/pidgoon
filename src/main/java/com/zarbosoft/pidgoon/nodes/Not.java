package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

public class Not extends Node {
	private final Node root;

	public Not(final Node root) {
		this.root = root;
	}

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
			public void error(final ParseContext step, final Store store, final Object cause) {
				parent.advance(step, store.pop(true), cause);
			}

			@Override
			public void advance(final ParseContext step, Store store, final Object cause) {
				store = store.pop(true);
				super.error(step, store, cause);
			}

			@Override
			public String buildPath(final String subpath) {
				return parent.buildPath(String.format("not . %s", subpath));
			}
		}, seen, cause);
	}

	public String toString() {
		final String out;
		if ((root instanceof Sequence) || (root instanceof Union) || (root instanceof Repeat)) {
			out = String.format("~(%s)", root);
		} else {
			out = String.format("~%s", root);
		}
		return out;
	}
}
