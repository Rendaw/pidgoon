package com.zarbosoft.undepurseable.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.GrammarPrivate;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;

public class Reference extends Node {

	public class RefParent implements Parent {
		Parent originalParent;
		List<Parent> loopParents = new ArrayList<>();

		public RefParent(Parent parent) {
			originalParent = parent;
		}

		public void advance(Position position, Store store) {
			if (cut) originalParent.cut(position);
			Clip data = store.popData();
			if (!drop) store.addData(data);
			originalParent.advance(position, store.split());
			for (Parent p : loopParents) {
				Store splitStore = store.split();
				splitStore.injectDataStack(p.size(this, 1));
				p.advance(position, splitStore.split());
			}
		}

		public String buildPath(String subpath) {
			return originalParent.buildPath(String.format("<%s> . %s", name, subpath));
		}

		@Override
		public void error(Position position, String string) {
			originalParent.error(position, string);
		}

		@Override
		public long size(Parent stopAt, long start) {
			if (stopAt == this) return start;
			return originalParent.size(stopAt, start + 1);
		}

		@Override
		public void cut(Position position) {
			originalParent.cut(position);
		}
	}

	private Node base = null;
	private String name;

	public Reference(String name) {
		super();
		this.name = name;
	}

	private Node get(GrammarPrivate grammar) {
		if (base == null) {
			base = grammar.getNode(name);
			drop = drop || base.drop;
		}
		return base;
	}

	@Override
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		if (seen.containsKey(name)) {
			seen.get(name).loopParents.add(parent);
			return;
		}
		RefParent subParent = new RefParent(parent);
		seen.put(name, subParent);
		get(startPosition.grammar).context(
			startPosition, 
			store.pushData(), 
			subParent, seen);
	}
	
	public String toString() {
		if (drop) return "#" + name;
		return name;
	}
}
