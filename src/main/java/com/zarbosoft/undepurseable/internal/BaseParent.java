package com.zarbosoft.undepurseable.internal;

public abstract class BaseParent implements Parent {

	private Parent parent;

	public BaseParent(Parent parent) {
		super();
		this.parent = parent;
	}
	
	public void error(TerminalContext leaf) {
		parent.error(leaf);
	}
	
	public long size(Parent stopAt, long start) {
		return parent.size(stopAt, start + 1);
	}

	public void cut(Position position) {
		parent.cut(position);
	}
}
