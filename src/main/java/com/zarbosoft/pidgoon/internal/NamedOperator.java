package com.zarbosoft.pidgoon.internal;

public abstract class NamedOperator extends Operator {

	public final String name;

	public NamedOperator(String name, Node root) {
		super(root);
		this.name = name;
	}

}
