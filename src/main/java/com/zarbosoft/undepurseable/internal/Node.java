package com.zarbosoft.undepurseable.internal;

import java.util.HashMap;
import java.util.Map;

import com.zarbosoft.undepurseable.nodes.Reference.RefParent;
import com.zarbosoft.undepurseable.source.Store;

public abstract class Node {
	public boolean drop = false;
	public boolean cut = false;

	public void context(ParseContext context, Store store, Parent parent) {
		context(context, store, parent, new HashMap<>());
	}
	
	public abstract void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen);

	public Node drop() {
		drop = true;
		return this;
	}

	public Node cut() {
		cut = true;
		return this;
	}
}
