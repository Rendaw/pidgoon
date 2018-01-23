package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

/**
 * Base node to match a single event.  Define `matches` to use.
 */
public abstract class Terminal extends Node {
	public Terminal() {
	}

	protected abstract boolean matches(final Event event);

	@Override
	public void context(
			final ParseContext context,
			final Store prestore,
			final Parent parent,
			final PMap<Object, RefParent> seen,
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
				if (matches(position.get())) {
					parent.advance(step, store, this);
				} else {
					parent.error(step, store, this);
				}
			}
		});
	}
}
