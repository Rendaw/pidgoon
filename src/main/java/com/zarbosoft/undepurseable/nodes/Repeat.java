package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.internal.BaseParent;
import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.ParseContext;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Repeat extends Node {
	private Node root;
	private Long min = 0L;
	private Long max;

	public Repeat(Node root) {
		super();
		this.root = root;
	}

	public Repeat min(long i) {
		min = i;
		return this;
	}

	public Repeat max(long i) {
		max = i;
		return this;
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		class RepParent extends BaseParent {
			long count;

			public RepParent(long count) {
				super(parent);
				this.count = count;
			}

			public void advance(Store store) {
				if (cut) parent.cut();
				Clip data = store.popData();
				if (!drop) store.addData(data);
				long nextCount = count + 1;
				if ((max != null) && (nextCount == max)) {
					parent.advance(store);
					return;
				} else {
					root.context(context, store.split().pushData(), new RepParent(nextCount));
					if ((min == null) || (nextCount >= min))
						parent.advance(store.split());
				}
			}
			
			@Override
			public String buildPath(String subpath) {
				return parent.buildPath(String.format(
					"rep*%d/%s%s . %s", 
					count + 1, 
					min == 0 ? "" : String.format("%s-", min.toString()), 
					max == null ? "*" : max.toString(), 
					subpath));
			}
		}
		root.context(context, store.split().pushData(), new RepParent(0), seen);
		if ((min == null) || (min == 0))
			parent.advance(store.split());
	}

	public String toString() {
		String out;
		if ((min == 0) && (max == null)) out = "*";
		else if ((min == 1) && (max == null)) out = "+";
		else if ((min == 0) && (max == 1)) out = "?";
		else out = String.format("{%d, %d}", min, max);
		if (!root.drop && (
				(root instanceof Sequence) ||
				(root instanceof Union))
			) {
			out = String.format("(%s)%s", root, out);
		} else {
			out = String.format("%s%s", root, out);
		}
		if (drop) return String.format("#(%s)", out);
		return out;
	}
}
