package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.Callback;
import com.zarbosoft.undepurseable.internal.BaseParent;
import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Capture extends Node {
	private Node root;
	private Callback callback;

	public Capture(Node root, Callback callback) {
		super();
		this.root = root;
		this.callback = callback;
	}

	@Override
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		root.context(startPosition, store.pushData(), new BaseParent(parent) {
			public void advance(Position position, Store store) {
				if (cut) parent.cut(position);
				callback.accept(store);
				Clip data = store.popData();
				if (!drop) store.addData(data);
				parent.advance(position, store);
			}

			@Override
			public String buildPath(String subpath) {
				return parent.buildPath(String.format("capture . %s", subpath));
			}
		}, seen);
	}

	public String toString() {
		String out = root.toString();
		if (drop && !root.drop) {
			if (
				(root instanceof Sequence) ||
				(root instanceof Union) ||
				(root instanceof Repeat)) {
				out = String.format("#(%s)", out);
			} else {
				out = String.format("#%s", out);
			}
		}
		return out;
	}
}
