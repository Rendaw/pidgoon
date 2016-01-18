package com.zarbosoft.undepurseable.internal;

public interface Parent {
	void advance(Position position, Store store);
	void error(Position position, String string);
	String buildPath(String rep);
}
