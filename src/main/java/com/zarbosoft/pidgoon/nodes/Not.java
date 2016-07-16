package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;
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
				if (cut)
					parent.cut(step);
				parent.advance(step, store.pop(!drop), cause);
			}

			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				store.pop(!drop);
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
		if (!root.drop && (
				(root instanceof Sequence) ||
						(root instanceof Union) ||
						(root instanceof Repeat)
		)) {
			out = String.format("~(%s)", root);
		} else {
			out = String.format("~%s", root);
		}
		if (drop)
			return "#" + out;
		return out;
	}
}
