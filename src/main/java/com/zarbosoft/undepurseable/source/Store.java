package com.zarbosoft.undepurseable.source;


public interface Store {

	Store split();

	default void popData() { this.popData(false); }
	
	void popData(boolean combine);

	Store pushData();

	void injectDataStack(long size);

	Object popStack();

	Store pushStack(Object o);

	void addData(Object storeData);

	boolean hasOneResult();

	Object takeResult();

}