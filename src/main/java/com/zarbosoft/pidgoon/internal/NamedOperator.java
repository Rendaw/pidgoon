package com.zarbosoft.pidgoon.internal;

public abstract class NamedOperator extends Operator {

	public final String name;

	public NamedOperator(final String name, final Node root) {
		super(root);
		this.name = name;
	}

	public NamedOperator(final String name) {
		super();
		this.name = name;
	}
}
