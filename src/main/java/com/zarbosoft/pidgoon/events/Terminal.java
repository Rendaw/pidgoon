package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

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
			final PMap<String, RefParent> seen,
			final Object cause
	) {
		final Node outer = this;
		context.leaves.add(new State() {
			@Override
			public String toString() {
				return parent.buildPath(outer.toString());
			}

			@Override
			public <T> T color() {
				return (T) prestore.color;
			}

			@Override
			public void parse(final ParseContext step, final com.zarbosoft.pidgoon.internal.Position sourcePosition) {
				Store store = (Store) prestore;
				final Position position = (Position) sourcePosition;
				store = store.record(position);
				if (value.matches(position.get())) {
					parent.advance(step, store, this);
				} else {
					parent.error(step, store, this);
				}
			}
		});
	}

}
