package com.zarbosoft.pidgoon.internal;

public interface Store {
	default Store pop() {
		return this.pop(false);
	}

	Store pop(boolean combine);

	Store push();

	Store inject(long size);

	<T> T stackTop();

	Store popStack();

	Store pushStack(Object o);

	boolean hasOneResult();

	Store record(Position position);

}