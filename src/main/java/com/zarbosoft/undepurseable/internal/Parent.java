package com.zarbosoft.undepurseable.internal;

public interface Parent {
	void advance(Position position, Store store);
	void error(Position position, String string);
	String buildPath(String rep);
	
	/**
	 * When handling infinite recursion, a parent chain is treated like a repeat,
	 * where the parent chan can be advanced through any number of times.
	 * Each iteration should have separate state - use this to split the state for each iteration.
	 * @param stopAt
	 * @return
	 */
	Parent clone(Parent stopAt);
}
