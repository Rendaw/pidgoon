package com.zarbosoft.pidgoon.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zarbosoft.pidgoon.internal.BaseParent;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

public class Sequence extends Node {
	List<Node> children = new ArrayList<>();
	
	public Sequence add(Node child) {
		children.add(child);
		return this;
	}

	@Override
	public void context(ParseContext context, Store store, Parent parent, Map<String, RefParent> seen) {
		class SeqParent extends BaseParent {
			final int step;

			public SeqParent(Parent parent, int step) {
				super(parent);
				this.step = step;
			}
			
			@Override
			public void advance(Store store) {
				store.pop(!drop);
				int nextStep = step + 1;
				if (nextStep >= children.size()) {
					if (cut) parent.cut();
					parent.advance(store);
				} else {
					children.get(nextStep).context(context, store.push(), new SeqParent(parent, nextStep));
				}
			}
			
			@Override
			public String buildPath(String subpath) {
				return parent.buildPath(String.format("seq[%d/%d] . %s", step + 1, children.size(), subpath));
			}
		}
		children.get(0).context(context, store.push(), new SeqParent(parent, 0), seen);
	}
	
	public String toString() {
		String out = children.stream()
			.map(c -> {
				if (!c.drop && (c instanceof Union)) return String.format("(%s)", c);
				return c.toString();
			})
			.collect(Collectors.joining(" "));
		if (drop) return String.format("#(%s)", out);
		return out;
	}
}
