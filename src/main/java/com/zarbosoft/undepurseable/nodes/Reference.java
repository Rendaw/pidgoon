package com.zarbosoft.undepurseable.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zarbosoft.undepurseable.internal.GrammarPrivate;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;

public class Reference extends Node {

	public class RefParent implements Parent {
		Parent originalParent;
		List<Parent> parents = new ArrayList<>();

		public RefParent(Parent parent) {
			originalParent = parent;
		}

		public void advance(Position position, Store store) {
			if (drop) store = store.drop();
			originalParent.advance(position, store.split());
			for (Parent p : parents)
				p.clone(this).advance(position, store.split());
		}

		public String buildPath(String subpath) {
			return originalParent.buildPath(String.format("<%s>.%s", name, subpath));
		}

		@Override
		public void error(Position position, String string) {
			originalParent.error(position, string);
		}

		@Override
		public Parent clone(Parent stopAt) {
			if (stopAt == this) return this;
			return new RefParent(originalParent.clone(stopAt));
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
	public void context(Position startPosition, Parent parent, Map<String, RefParent> seen) {
		if (seen.containsKey(name)) {
			seen.get(name).parents.add(parent);
			return;
		}
		RefParent subParent = new RefParent(parent);
		seen.put(name, subParent);
		get(startPosition.grammar).context(
			startPosition, 
			subParent, 
			seen);
	}
	
	public String toString() {
		if (drop) return "#" + name;
		return name;
	}
}
