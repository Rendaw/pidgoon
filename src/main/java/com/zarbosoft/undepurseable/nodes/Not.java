package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.internal.Mutable;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.ParseContext;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.internal.TerminalReader;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Not extends Node {
	private Node root;

	public Not(Node root) {
		this.root = root;
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		// Order is significant - custom terminal context behavior based on comparison
		Mutable<Boolean> mutable = new Mutable<>(null);
		root.context(context, store.split(), new Parent() {
			public void error(TerminalReader leaf) {
				mutable.value = true;
			}

			@Override
			public void advance(Store store) {
				mutable.value = false;
			}

			@Override
			public String buildPath(String subpath) {
				return parent.buildPath("(ignore: not pattern)");
			}

			@Override
			public long size(Parent stopAt, long start) {
				return parent.size(stopAt, start + 1);
			}

			@Override
			public void cut() {
			}
		}, seen);
		context.outLeaves.add(new TerminalReader() {
			@Override
			public String toString() {
				return parent.buildPath(String.format("not: %s", root));
			}
			@Override
			public void parse() {
				if (mutable.value == false) {
					parent.error(this);
					return;
				}
				if (!drop) store.addData(context.position.getStoreData());
				if (mutable.value == true) {
					if (cut) parent.cut();
					parent.advance(store);
				} else {
					context.outLeaves.add(this);
				}
			}
		});
	}

	public String toString() {
		String out;
		if (!root.drop && (
				(root instanceof Sequence) ||
				(root instanceof Union) ||
				(root instanceof Repeat))
			) {
			out = String.format("~(%s)", root);
		} else {
			out = String.format("~%s", root);
		}
		if (drop) return "#" + out;
		return out;
	}
}
