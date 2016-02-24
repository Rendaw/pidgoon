package com.zarbosoft.pidgoon.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.bytes.Callback;

public abstract class BaseParse<P extends BaseParse<P>> {

	protected Grammar grammar;
	protected String node;
	protected Supplier<Object> initialStack;
	protected Map<String, Object> callbacks;
	
	public BaseParse() {
		super();
	}

	public BaseParse(BaseParse<P> other) {
		grammar = other.grammar;
		node = other.node;
		initialStack = other.initialStack;
		callbacks = other.callbacks;
	}

	public P grammar(Grammar grammar) {
		if (this.grammar != null) throw new IllegalArgumentException("Grammar already specified");
		P out = split();
		out.grammar = grammar;
		return out;
	}

	public P node(String node) {
		if (this.node != null) throw new IllegalArgumentException("Node already specified");
		P out = split();
		out.node = node;
		return out;
	}

	public P stack(Supplier<Object> supplier) {
		if (this.initialStack != null) throw new IllegalArgumentException("Initial stack supplier already specified");
		P out = split();
		out.initialStack = supplier;
		return out;
	}

	public P callbacks(Map<String, Callback> callbacks) {
		if (this.callbacks != null) throw new IllegalArgumentException("Callbacks already specified");
		if (callbacks == null) return (P)this;
		P out = split();
		Map<String, Object> newCallbacks = new HashMap<>();
		callbacks.forEach((k, v) -> newCallbacks.put(k, v));
		out.callbacks = newCallbacks;
		return out;
	}

	abstract protected P split();
}