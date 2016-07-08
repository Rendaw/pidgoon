package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Position;
import com.zarbosoft.pidgoon.source.Store;

import java.util.Map;

public class Wildcard extends Node {
	public String toString() {
		return ".";
	}

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final Map<String, RefParent> seen,
			final Object cause
	) {
		context.outLeaves.add(new State() {
			@Override
			public String toString() {
				return parent.buildPath(".");
			}

			@Override
			public void parse(final ParseContext step, final Position position) {
				if (cut) parent.cut(step);
				Store tempStore = store;
				if (!drop) tempStore = store.record(position);
				parent.advance(step, tempStore, this);
			}
		});
	}

}
