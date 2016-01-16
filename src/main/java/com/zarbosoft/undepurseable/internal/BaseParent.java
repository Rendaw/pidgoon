package com.zarbosoft.undepurseable.internal;

public abstract class BaseParent implements Parent {

	private Parent parent;

	public BaseParent(Parent parent) {
		super();
		this.parent = parent;
	}
	
	public abstract void advance(Position position, Store store);

	public void error(Position position, String string) {
		parent.error(position, string);
	}

	public abstract String buildPath(String rep);
}
