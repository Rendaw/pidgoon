package com.zarbosoft.pidgoon.events;

import java.util.Map;

import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.internal.TerminalReader;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

public class Terminal extends Node {
	private Event value;
	
	public Terminal(Event value) {
		this.value = value;
	}

	public String toString() {
		return String.format("'%s'", value.toString());
	}

	@Override
	public void context(ParseContext context, Store prestore, Parent parent, Map<String, RefParent> seen) {
		Node outer = this;
		context.outLeaves.add(new TerminalReader() {
			@Override
			public String toString() {
				return parent.buildPath(outer.toString());
			}
			
			@Override
			public void parse() {
				Store store = (Store)prestore;
				Position position = (Position) context.position;
				if (value.matches(position.get())) {
					if (cut) parent.cut();
					if (!drop) store.record(position);
					parent.advance(store);
				} else {
					parent.error(this);
				}
			}
		});
	}

}
