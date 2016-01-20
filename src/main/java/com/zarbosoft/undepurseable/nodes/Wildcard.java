package com.zarbosoft.undepurseable.nodes;

import java.util.Map;

import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.ParseContext;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.internal.TerminalReader;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

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
				if (!drop) store.addData(new Clip(context.position));
				parent.advance(store);
			}
		});
	}

}
