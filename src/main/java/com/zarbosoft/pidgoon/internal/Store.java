package com.zarbosoft.pidgoon.internal;

/**
 * This contains user-accessible state for the parse.  This is immutable - any modifications return a new Store.  This
 * is because parses can branch.
 */
public abstract class Store {
	public Object color;

	public Store pop() {
		return this.pop(false);
	}

	public abstract Store pop(boolean combine);

	public abstract Store push();

	public abstract Store inject(long size);

	/**
	 * Get the top of the user computational stack without creating any changes.
	 *
	 * @param <T>
	 * @return
	 */
	public abstract <T> T stackTop();

	/**
	 * Pop an element from the top of the user computational stack.
	 *
	 * @return
	 */
	public abstract Store popStack();

	/**
	 * Push an element to the top of the user computational stack.
	 *
	 * @param o
	 * @return
	 */
	public abstract Store pushStack(Object o);

	public abstract boolean hasOneResult();

	public abstract Store record(Position position);

}