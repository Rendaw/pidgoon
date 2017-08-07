package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

public abstract class Node {
	public void context(final ParseContext context, final Store store, final Parent parent, final Object cause) {
		context(context, store, parent, HashTreePMap.empty(), cause);
	}

	public abstract void context(
			ParseContext context, Store store, Parent parent, PMap<Object, RefParent> seen, Object cause
	);
}
