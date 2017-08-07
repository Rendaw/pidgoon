package com.zarbosoft.pidgoon.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseParse<P extends BaseParse<P>> {

	protected Grammar grammar;
	protected String node = "root";
	protected Supplier<Object> initialStack;
	protected Map<Object, Object> callbacks;
	protected int errorHistoryLimit;
	protected int uncertaintyLimit;
	protected boolean dumpAmbiguity;

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
		dumpAmbiguity = other.dumpAmbiguity;
	}

	public P grammar(final Grammar grammar) {
		if (this.grammar != null)
			throw new IllegalArgumentException("Grammar already specified");
		if (grammar == null)
			throw new IllegalArgumentException("Argument is null.");
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

	public P callbacks(final Map<Object, ? extends Callback<?>> callbacks) {
		if (this.callbacks != null)
			throw new IllegalArgumentException("Callbacks already specified");
		if (callbacks == null)
			return (P) this;
		final P out = split();
		final Map<Object, Object> newCallbacks = new HashMap<>();
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

	public P dumpAmbiguity(final boolean dumpAmbiguity) {
		if (this.dumpAmbiguity)
			throw new IllegalArgumentException("Dump ambiguity already specified");
		final P out = split();
		out.dumpAmbiguity = dumpAmbiguity;
		return out;
	}
}