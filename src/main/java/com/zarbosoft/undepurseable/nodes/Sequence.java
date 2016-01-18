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
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		class SeqParent extends BaseParent {
			final int step;

			public SeqParent(Parent parent, int step) {
				super(parent);
				this.step = step;
			}
			
			public void advance(Position position, Store store) {
				if (drop) store = store.drop();
				System.out.println(String.format(
					"Seq %d %d store %d : stack %s, data [%s]", 
					step,
					System.identityHashCode(this),
					System.identityHashCode(store),
					store.stack.stream()
						.map(o -> o.toString())
						.collect(Collectors.joining(", ")),
					store.dataString()
				));
				int nextStep = step + 1;
				if (nextStep >= children.size())
					parent.advance(position, store);
				else {
					children.get(nextStep).context(position, store, new SeqParent(parent, nextStep));
				}
			}
			
			@Override
			public String buildPath(String subpath) {
				return parent.buildPath(String.format("seq[%d/%d].%s", step + 1, children.size(), subpath));
			}
		}
		children.get(0).context(startPosition, store, new SeqParent(parent, 0), seen);
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
