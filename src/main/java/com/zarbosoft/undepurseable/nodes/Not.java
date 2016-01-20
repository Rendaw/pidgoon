package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Mutable;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.internal.TerminalContext;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Not extends Node {
	private Node root;

	public Not(Node root) {
		this.root = root;
	}

	@Override
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		// Order is significant - custom terminal context behavior based on comparison
		Mutable<Boolean> mutable = new Mutable<>(null);
		root.context(startPosition, store.split(), new Parent() {
			public void error(Position position, String string) {
				mutable.value = true;
			}

			public void advance(Position position, Store store) {
				mutable.value = false;
			}

			public String buildPath(String subpath) {
				return parent.buildPath("(ignore: not pattern)");
			}

			@Override
			public long size(Parent stopAt, long start) {
				return parent.size(stopAt, start + 1);
			}

			@Override
			public void cut(Position position) {
			}
		}, seen);
		startPosition.addLeaf(new TerminalContext() {
			@Override
			public String toString() {
				return parent.buildPath(String.format("not: %s", root));
			}
			@Override
			public void parse(Position position) {
				if (mutable.value == false) {
					parent.error(position, toString());
					return;
				}
				if (!drop)
					store.addData(new Clip(position));
				if (mutable.value == true) {
					if (cut) parent.cut(position);
					parent.advance(position, store);
				} else {
					position.addLeaf(this);
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
