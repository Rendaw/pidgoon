package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

import java.util.Map;

public class Terminal extends Node {
	private final Event value;

	public Terminal(final Event value) {
		this.value = value;
	}

	public String toString() {
		return String.format("'%s'", value.toString());
	}

	@Override
	public void context(
			final ParseContext context,
			final Store prestore,
			final Parent parent,
			final Map<String, RefParent> seen,
			final Object cause
	) {
		final Node outer = this;
		context.outLeaves.add(new State() {
			@Override
			public String toString() {
				return parent.buildPath(outer.toString());
			}

			@Override
			public void parse(final ParseContext step, final com.zarbosoft.pidgoon.source.Position sourcePosition) {
				Store store = (Store) prestore;
				final Position position = (Position) sourcePosition;
				if (!drop) store = store.record(position);
				if (value.matches(position.get())) {
					if (cut) parent.cut(step);
					parent.advance(step, store, this);
				} else {
					parent.error(step, store, this);
				}
			}
		});
	}

}
