package com.zarbosoft.undepurseable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Deque;

import com.zarbosoft.undepurseable.internal.GrammarPrivate;
import com.zarbosoft.undepurseable.internal.Node;

public class Grammar {
	private GrammarPrivate p = new GrammarPrivate();
	
	public void add(String name, Node node) {
		p.add(name,  node);
	}

	public Deque<Object> parse(String node, InputStream stream) throws IOException {
		return p.parse(node, stream);
	}

	public Deque<Object> parse(String node, String string) throws IOException {
		return parse(node, new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}
	
	public String toString() {
		return p.toString();
	}
}
