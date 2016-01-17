package com.zarbosoft.undepurseable.internal;

import java.util.HashMap;
import java.util.Map;

import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public abstract class Node {
	public boolean drop = false;

	public void context(Position startPosition, Parent parent) {
		context(startPosition, parent, new HashMap<>());
	}
	
	public abstract void context(Position startPosition, Parent parent, Map<String, RefParent> seen);

	public Node drop() {
		drop = true;
		return this;
	}
}
