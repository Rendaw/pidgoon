package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.ParseContext;
import com.zarbosoft.undepurseable.internal.TerminalReader;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;
import com.zarbosoft.undepurseable.source.Store;

public class Wildcard extends Node {
	public String toString() {
		return ".";
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		context.outLeaves.add(new TerminalReader() {
			@Override
			public String toString() {
				return parent.buildPath(".");
			}
			
			@Override
			public void parse() {
				if (cut) parent.cut();
				if (!drop) store.addData(context.position.getStoreData());
				parent.advance(store);
			}
		});
	}

}
