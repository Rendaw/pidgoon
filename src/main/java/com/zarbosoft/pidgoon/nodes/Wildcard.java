package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Position;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

public class Wildcard extends Node {
	public String toString() {
		return ".";
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<String, RefParent> seen,
			final Object cause
	) {
		context.leaves.add(new State() {
			@Override
			public String toString() {
				return parent.buildPath(".");
			}

			@Override
			public <T> T color() {
				return (T) store.color;
			}

			@Override
			public void parse(final ParseContext step, final Position position) {
				parent.advance(step, store.record(position), this);
			}
		});
	}

}
