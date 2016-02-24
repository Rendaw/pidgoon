package com.zarbosoft.pidgoon.source;


public interface Store {

	Store split();

	default void pop() { this.pop(false); }
	
	void pop(boolean combine);

	Store push();

	void inject(long size);

	Object popStack();

	Store pushStack(Object o);

	boolean hasOneResult();

	Object takeResult();

	void record(Position position);

}