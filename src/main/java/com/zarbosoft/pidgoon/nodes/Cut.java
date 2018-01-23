package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Store;
import org.pcollections.PMap;

/**
 * Drop all current branches except this when the child node matches. This is for when you're sure this is the correct
 * parse and want to save memory.
 */
public class Cut extends Node {
	private final Node child;

	public Cut(final Node child) {
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
		child.context(context, store, new BaseParent(parent) {

			@Override
			public void advance(final ParseContext step, final Store store, final Object cause) {
				parent.cut(step);
				parent.advance(step, store, cause);
			}

			@Override
			public String buildPath(final String subpath) {
				return parent.buildPath(String.format("! . %s", subpath));
			}
		}, seen, cause);
	}
}
