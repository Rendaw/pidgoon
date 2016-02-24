package com.zarbosoft.pidgoon.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.bytes.Callback;

public abstract class BaseParse<O> {

	protected Grammar grammar;
	protected String node;
	protected Supplier<Object> initialStack;
	protected Map<String, Object> callbacks;
	
	public BaseParse() {
		super();
	}

	public BaseParse(BaseParse<O> other) {
		grammar = other.grammar;
		node = other.node;
		initialStack = other.initialStack;
		callbacks = other.callbacks;
	}

	public BaseParse<O> grammar(Grammar grammar) {
		if (this.grammar != null) throw new IllegalArgumentException("Grammar already specified");
		BaseParse<O> out = split();
		out.grammar = grammar;
		return out;
	}

	public BaseParse<O> node(String node) {
		if (this.node != null) throw new IllegalArgumentException("Node already specified");
		BaseParse<O> out = split();
		out.node = node;
		return out;
	}

	public BaseParse<O> stack(Supplier<Object> supplier) {
		if (this.initialStack != null) throw new IllegalArgumentException("Initial stack supplier already specified");
		BaseParse<O> out = split();
		out.initialStack = supplier;
		return out;
	}

	public BaseParse<O> callbacks(Map<String, Callback> callbacks) {
		if (this.callbacks != null) throw new IllegalArgumentException("Callbacks already specified");
		if (callbacks == null) return this;
		BaseParse<O> out = split();
		Map<String, Object> newCallbacks = new HashMap<>();
		callbacks.forEach((k, v) -> newCallbacks.put(k, v));
		out.callbacks = newCallbacks;
		return out;
	}

	public O parse(String string) throws IOException {
		return parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}

	abstract protected BaseParse<O> split();
	
	abstract public O parse(InputStream stream) throws IOException;
}