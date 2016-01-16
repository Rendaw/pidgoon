package com.zarbosoft.undepurseable.nodes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.primitives.Bytes;
import com.zarbosoft.undepurseable.internal.BaseParent;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

public class Sequence extends Node {
	List<Node> children = new ArrayList<>();
	
	public Sequence add(Node child) {
		children.add(child);
		return this;
	}
	
	public static Sequence bytes(List<Byte> list) {
		Sequence out = new Sequence();
		list.stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public static Sequence string(String string) {
		Sequence out = new Sequence();
		Bytes.asList(string.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	@Override
	public void context(Position startPosition, Store startStore, Parent parent, Map<String, RefParent> seen) {
		class SeqParent extends BaseParent {
			final int step;
			
			public SeqParent(int step) {
				super(parent);
				this.step = step;
			}

			public void advance(Position position, Store store) {
				if (drop) store.dropData(startStore);
				int nextStep = step + 1;
				if (nextStep >= children.size())
					parent.advance(position, store);
				else {
					children.get(nextStep).context(position, store, new SeqParent(nextStep));
				}
			}
			
			@Override
			public String buildPath(String subpath) {
				return parent.buildPath(String.format("seq[%d/%d].%s", step + 1, children.size(), subpath));
			}
		}
		children.get(0).context(startPosition, startStore, new SeqParent(0), seen);
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
