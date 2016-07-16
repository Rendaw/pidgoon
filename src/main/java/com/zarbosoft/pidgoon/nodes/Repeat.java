package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;
import org.pcollections.PMap;

public class Repeat extends Node {
	private final Node root;
	private Long min = 0L;
	private Long max;

	public Repeat(final Node root) {
		super();
		this.root = root;
	}

	public Repeat min(final long i) {
		min = i;
		return this;
	}

	public Repeat max(final long i) {
		max = i;
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
		class RepParent extends BaseParent {
			long count;

			public RepParent(final long count) {
				super(parent);
				this.count = count;
			}

			public void advance(final ParseContext step, final Store store, final Object cause) {
				if (cut)
					parent.cut(step);
				final Store tempStore = store.pop(!drop);
				final long nextCount = count + 1;
				if ((max != null) && (nextCount == max)) {
					parent.advance(step, tempStore, cause);
					return;
				} else {
					root.context(step, tempStore.push(), new RepParent(nextCount), cause);
					if ((min == null) || (nextCount >= min))
						parent.advance(step, tempStore, cause);
				}
			}

			@Override
			public String buildPath(final String subpath) {
				return parent.buildPath(String.format(
						"rep*%d/%s%s . %s",
						count + 1,
						min == 0 ? "" : String.format("%s-", min.toString()),
						max == null ? "*" : max.toString(),
						subpath
				));
			}
		}
		root.context(context, store.push(), new RepParent(0), seen, cause);
		if ((min == null) || (min == 0))
			parent.advance(context, store, cause);
	}

	public String toString() {
		String out;
		if ((min == 0) && (max == null))
			out = "*";
		else if ((min == 1) && (max == null))
			out = "+";
		else if ((min == 0) && (max == 1))
			out = "?";
		else
			out = String.format("{%d, %d}", min, max);
		if (!root.drop && (
				(root instanceof Sequence) || (root instanceof Union)
		)) {
			out = String.format("(%s)%s", root, out);
		} else {
			out = String.format("%s%s", root, out);
		}
		if (drop)
			return String.format("#(%s)", out);
		return out;
	}
}
