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
		private Store startStore;
		List<Parent> parents = new ArrayList<>();

		public RefParent(Parent parent, Store startStore) {
			this.startStore = startStore;
			parents.add(parent);
		}

		public void advance(Position position, Store store) {
			if (drop) store.dropData(startStore);
			parents.forEach(p -> p.advance(position, store));
		}

		public String buildPath(String subpath) {
			return parents.get(0).buildPath(String.format("<%s>.%s", name, subpath));
		}

		@Override
		public void error(Position position, String string) {
			parents.get(0).error(position, string);
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
	public void context(Position startPosition, Store startStore, Parent parent, Map<String, RefParent> seen) {
		if (seen.containsKey(name)) {
			seen.get(name).parents.add(parent);
			return;
		}
		RefParent subParent = new RefParent(parent, startStore);
		seen.put(name, subParent);
		get(startPosition.grammar).context(
			startPosition, 
			startStore, 
			subParent, 
			seen);
	}
	
	public String toString() {
		if (drop) return "#" + name;
		return name;
	}
}
