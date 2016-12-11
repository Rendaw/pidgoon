package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.bytes.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseParse<P extends BaseParse<P>> {

	protected Grammar grammar;
	protected String node = "root";
	protected Supplier<Object> initialStack;
	protected Map<String, Object> callbacks;
	protected int errorHistoryLimit;
	protected int uncertaintyLimit;

	public BaseParse() {
		super();
		errorHistoryLimit = 1;
		uncertaintyLimit = 1000;
	}

	public BaseParse(final BaseParse<P> other) {
		grammar = other.grammar;
		node = other.node;
		initialStack = other.initialStack;
		callbacks = other.callbacks;
		errorHistoryLimit = other.errorHistoryLimit;
		uncertaintyLimit = other.uncertaintyLimit;
	}

	public P grammar(final Grammar grammar) {
		if (this.grammar != null)
			throw new IllegalArgumentException("Grammar already specified");
		final P out = split();
		out.grammar = grammar;
		return out;
	}

	public P node(final String node) {
		if (!this.node.equals("root"))
			throw new IllegalArgumentException("Node already specified");
		final P out = split();
		out.node = node;
		return out;
	}

	public P stack(final Supplier<Object> supplier) {
		if (this.initialStack != null)
			throw new IllegalArgumentException("Initial stack supplier already specified");
		final P out = split();
		out.initialStack = supplier;
		return out;
	}

	public P callbacks(final Map<String, Callback> callbacks) {
		if (this.callbacks != null)
			throw new IllegalArgumentException("Callbacks already specified");
		if (callbacks == null)
			return (P) this;
		final P out = split();
		final Map<String, Object> newCallbacks = new HashMap<>();
		callbacks.forEach((k, v) -> newCallbacks.put(k, v));
		out.callbacks = newCallbacks;
		return out;
	}

	public P errorHistory(final int limit) {
		if (this.errorHistoryLimit != 1)
			throw new IllegalArgumentException("Error history limit already specified");
		final P out = split();
		out.errorHistoryLimit = limit;
		return out;
	}

	public P uncertainty(final int limit) {
		if (this.uncertaintyLimit != 1000)
			throw new IllegalArgumentException("Uncertainty limit already specified");
		final P out = split();
		out.uncertaintyLimit = limit;
		return out;
	}

	abstract protected P split();
}