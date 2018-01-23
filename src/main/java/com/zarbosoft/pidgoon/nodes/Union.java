package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zarbosoft.rendaw.common.Common.enumerate;

/**
 * Match exactly one child.
 */
public class Union extends Node {
	List<Node> children = new ArrayList<>();

	public Union add(final Node child) {
		children.add(child);
		return this;
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<Object, RefParent> seen,
			final Object cause
	) {
		enumerate(children.stream()).forEach(p -> {
			p.second.context(context, store.push(), new BaseParent(parent) {
				@Override
				public void advance(final ParseContext step, final Store store, final Object cause) {
					parent.advance(step, store.pop(true), cause);
				}

				@Override
				public String buildPath(final String subpath) {
					return parent.buildPath(String.format("union|%d . %s", p.first + 1, subpath));
				}
			}, seen, cause);
		});
	}

	public String toString() {
		return children.stream().map(c -> c.toString()).collect(Collectors.joining(" | "));
	}
}
