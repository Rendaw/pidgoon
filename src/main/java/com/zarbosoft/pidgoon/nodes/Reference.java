package com.zarbosoft.pidgoon.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.internal.TerminalReader;
import com.zarbosoft.pidgoon.source.Store;

public class Reference extends Node {

	public class RefParent implements Parent {
		Parent originalParent;
		List<Parent> loopParents = new ArrayList<>();

		public RefParent(Parent parent) {
			originalParent = parent;
		}

		public void advance(Store store) {
			if (cut) originalParent.cut();
			store.pop(!drop);
			originalParent.advance(store.split());
			for (Parent p : loopParents) {
				Store splitStore = store.split(); // TODO split twice - fix?
				splitStore.inject(p.size(this, 1));
				p.advance(splitStore.split());
			}
		}

		public String buildPath(String subpath) {
			return originalParent.buildPath(String.format("<%s> . %s", name, subpath));
		}

		@Override
		public void error(TerminalReader leaf) {
			originalParent.error(leaf);
		}

		@Override
		public long size(Parent stopAt, long start) {
			if (stopAt == this) return start;
			return originalParent.size(stopAt, start + 1);
		}

		@Override
		public void cut() {
			originalParent.cut();
		}
	}

	private Node base = null;
	private String name;

	public Reference(String name) {
		super();
		this.name = name;
	}

	private Node get(ParseContext context) {
		if (base == null) {
			base = context.grammar.getNode(name);
			drop = drop || base.drop;
		}
		return base;
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		if (seen.containsKey(name)) {
			seen.get(name).loopParents.add(parent);
			return;
		}
		RefParent subParent = new RefParent(parent);
		seen.put(name, subParent);
		get(context).context(
			context,
			store.push(), 
			subParent, seen);
	}
	
	public String toString() {
		if (drop) return "#" + name;
		return name;
	}
}
