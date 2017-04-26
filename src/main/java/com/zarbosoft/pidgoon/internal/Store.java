package com.zarbosoft.pidgoon.internal;

public abstract class Store {
	public Object color;

	public Store pop() {
		return this.pop(false);
	}

	public abstract Store pop(boolean combine);

	public abstract Store push();

	public abstract Store inject(long size);

	public abstract <T> T stackTop();

	public abstract Store popStack();

	public abstract Store pushStack(Object o);

	public abstract boolean hasOneResult();

	public abstract Store record(Position position);

}