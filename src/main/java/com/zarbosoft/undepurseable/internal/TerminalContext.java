package com.zarbosoft.undepurseable.internal;

public abstract class TerminalContext {
	protected Store store = new Store();

	public abstract void parse(Position position);

}
