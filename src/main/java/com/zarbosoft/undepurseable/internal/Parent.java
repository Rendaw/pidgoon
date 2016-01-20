package com.zarbosoft.undepurseable.internal;

public interface Parent {
	void advance(Position position, Store store);
	void error(TerminalContext leaf);
	String buildPath(String rep);
	long size(Parent stopAt, long start);
	void cut(Position position);
}
