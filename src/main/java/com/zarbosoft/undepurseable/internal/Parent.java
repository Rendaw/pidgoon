package com.zarbosoft.undepurseable.internal;

import com.zarbosoft.undepurseable.source.Store;

public interface Parent {
	void advance(Store storeI);
	void error(TerminalReader leaf);
	String buildPath(String rep);
	long size(Parent stopAt, long start);
	void cut();
}
