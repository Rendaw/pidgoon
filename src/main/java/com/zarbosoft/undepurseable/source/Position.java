package com.zarbosoft.undepurseable.source;

import java.io.IOException;

public interface Position {

	String toString();

	Position advance() throws IOException;

	boolean isEOF();

	long getAbsolute();

	Object getStoreData();

}