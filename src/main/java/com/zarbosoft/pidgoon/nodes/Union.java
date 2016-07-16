package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.internal.*;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
			final PMap<String, RefParent> seen,
			final Object cause
	) {
		Pair.enumerate(children).forEach(p -> {
			p.second.context(context, store.push(), new BaseParent(parent) {
				@Override
				public void advance(final ParseContext step, final Store store, final Object cause) {
					if (cut)
						parent.cut(step);
					parent.advance(step, store.pop(!drop), cause);
				}

				@Override
				public String buildPath(final String subpath) {
					return parent.buildPath(String.format("union|%d . %s", p.first + 1, subpath));
				}
			}, seen, cause);
		});
	}

	public String toString() {
		final String out = children.stream().map(c -> c.toString()).collect(Collectors.joining(" | "));
		if (drop)
			return String.format("#(%s)", out);
		return out;
	}
}
