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

public class Sequence extends Node {
	List<Node> children = new ArrayList<>();

	public Sequence add(final Node child) {
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
		if (children.isEmpty()) {
			parent.advance(context, store, cause);
		} else {
			class SeqParent extends BaseParent {
				final int step;

				public SeqParent(final Parent parent, final int step) {
					super(parent);
					this.step = step;
				}

				@Override
				public void advance(final ParseContext step, final Store store, final Object cause) {
					final Store tempStore = store.pop(true);
					final int nextStep = this.step + 1;
					if (nextStep >= children.size()) {
						parent.advance(step, tempStore, cause);
					} else {
						children.get(nextStep).context(step, tempStore.push(), new SeqParent(parent, nextStep), cause);
					}
				}

				@Override
				public String buildPath(final String subpath) {
					return parent.buildPath(String.format("seq[%d/%d] . %s", step + 1, children.size(), subpath));
				}
			}
			children.get(0).context(context, store.push(), new SeqParent(parent, 0), seen, cause);
		}
	}

	public String toString() {
		final String out = children.stream().map(c -> {
			if (c instanceof Union)
				return String.format("(%s)", c);
			return c.toString();
		}).collect(Collectors.joining(" "));
		return out;
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}
}
