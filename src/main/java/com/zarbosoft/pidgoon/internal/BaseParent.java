package com.zarbosoft.pidgoon.internal;

public abstract class BaseParent implements Parent {

	private Parent parent;

	public BaseParent(Parent parent) {
		super();
		this.parent = parent;
	}
	
	public void error(TerminalReader leaf) {
		parent.error(leaf);
	}
	
	public long size(Parent stopAt, long start) {
		return parent.size(stopAt, start + 1);
	}

	public void cut() {
		parent.cut();
	}
}
