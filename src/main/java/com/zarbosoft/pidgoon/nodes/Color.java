package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Store;
import org.pcollections.PMap;

public class Color extends Node {
	private final Object color;
	public final Node child;

	public Color(final Object color, final Node child) {
		this.color = color;
		this.child = child;
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<Object, Reference.RefParent> seen,
			final Object cause
	) {
		store.color = color;
		child.context(context, store, new BaseParent(parent) {
			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				parent.advance(step, store, cause);
			}

			@Override
			public String buildPath(final String subpath) {
				return parent.buildPath(String.format("color . %s", subpath));
			}
		}, seen, cause);
	}
}
