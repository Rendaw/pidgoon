package com.zarbosoft.undepurseable.bytes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.zarbosoft.undepurseable.bytes.internal.ClipStore;
import com.zarbosoft.undepurseable.bytes.internal.Position;
import com.zarbosoft.undepurseable.internal.GrammarPrivate;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.nodes.Sequence;

public class Grammar {
	// TODO builder
	
	private GrammarPrivate p = new GrammarPrivate();
	
	public void add(String name, Node node) {
		p.add(name,  node);
	}

	public Object parse(String node, InputStream stream, Object initialStack) throws IOException {
		ClipStore store = new ClipStore();
		if (initialStack != null)
			store.pushStack(initialStack);
		return p.parse(node, new Position(stream), store);
	}

	public Object parse(String node, String string, Object initialStack) throws IOException {
		return parse(node, new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)), initialStack);
	}

	public Object parse(String node, String string) throws IOException {
		return parse(node, string, null);
	}
	
	public String toString() {
		return p.toString();
	}
	
	public static Sequence byteSeq(List<Byte> list) {
		Sequence out = new Sequence();
		list.stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public static Sequence stringSeq(String string) {
		Sequence out = new Sequence();
		Bytes.asList(string.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

}
