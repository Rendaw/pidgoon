package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

import java.util.HashMap;
import java.util.Map;

public abstract class Node {
	public boolean drop = false;
	public boolean cut = false;

	public void context(final ParseContext context, final Store store, final Parent parent, final Object cause) {
		context(context, store, parent, new HashMap<>(), cause);
	}

	public abstract void context(
			ParseContext context,
			Store store,
			Parent parent,
			Map<String, RefParent> seen,
			Object cause
	);

	public Node drop() {
		drop = true;
		return this;
	}

	public Node cut() {
		cut = true;
		return this;
	}
}
