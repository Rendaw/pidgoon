package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.internal.TerminalContext;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Wildcard extends Node {
	public String toString() {
		return ".";
	}

	@Override
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		startPosition.addLeaf(new TerminalContext() {
			@Override
			public String toString() {
				return parent.buildPath(".");
			}
			
			@Override
			public void parse(Position position) {
				if (cut) parent.cut(position);
				if (!drop) store.addData(new Clip(position));
				parent.advance(position, store);
			}
		});
	}

}
