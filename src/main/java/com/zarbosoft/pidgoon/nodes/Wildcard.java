package com.zarbosoft.pidgoon.nodes;

import java.util.Map;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.internal.TerminalReader;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

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
				if (!drop) store.record(context.position);
				parent.advance(store);
			}
		});
	}

}
