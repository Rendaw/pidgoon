package com.zarbosoft.pidgoon.internal;

import java.util.Map;

import com.zarbosoft.pidgoon.nodes.Reference;
import com.zarbosoft.pidgoon.nodes.Repeat;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.pidgoon.nodes.Union;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

public abstract class BaseCapture extends Node {
	private Node root;

	public BaseCapture(Node root) {
		super();
		this.root = root;
	}
	
	protected abstract void callback(Store store);

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		root.context(context, store.pushData(), new BaseParent(parent) {
			
			@Override
			public void advance(Store store) {
				if (cut) parent.cut();
				callback(store);
				store.popData(!drop);
				parent.advance(store);
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
