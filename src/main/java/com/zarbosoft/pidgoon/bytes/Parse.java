package com.zarbosoft.pidgoon.bytes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.primitives.Bytes;
import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.nodes.Sequence;

public class Parse<O> {
	private Grammar grammar;
	private String node;
	private Supplier<Object> initialStack;
	private Map<String, Object> callbacks;
	
	private Parse(Parse<O> other) {
		grammar = other.grammar;
		node = other.node;
		initialStack = other.initialStack;
	}

	public Parse() {}

	public Parse<O> grammar(Grammar grammar) {
		if (this.grammar != null) throw new IllegalArgumentException("Grammar already specified");
		Parse<O> out = new Parse<>(this);
		out.grammar = grammar;
		return out;
	}
	
	public Parse<O> node(String node) {
		if (this.node != null) throw new IllegalArgumentException("Node already specified");
		Parse<O> out = new Parse<>(this);
		out.node = node;
		return out;
	}

	public Parse<O> stack(Supplier<Object> supplier) {
		if (this.initialStack != null) throw new IllegalArgumentException("Initial stack supplier already specified");
		Parse<O> out = new Parse<>(this);
		out.initialStack = supplier;
		return out;
	}

	public Parse<O> callbacks(Map<String, Callback> callbacks) {
		if (this.callbacks != null) throw new IllegalArgumentException("Callbacks already specified");
		Parse<O> out = new Parse<>(this);
		Map<String, Object> newCallbacks = new HashMap<>();
		callbacks.forEach((k, v) -> newCallbacks.put(k, v));
		out.callbacks = newCallbacks;
		return out;
	}
	
	public O parse(String string) throws IOException {
		return parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}
	
	@SuppressWarnings("unchecked")
	public O parse(InputStream stream) throws IOException {
		ClipStore store = new ClipStore();
		if (initialStack != null)
			store.pushStack(initialStack.get());
		return (O) grammar.parse(node, new Position(stream), callbacks, store);
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
