package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.ParseContext;

public interface Parent {
	void advance(ParseContext step, Store store, Object cause);

	void error(ParseContext step, Store store, Object cause);

	String buildPath(String rep);

	long size(Parent stopAt, long start);

	void cut(ParseContext step);
}
