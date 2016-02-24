package com.zarbosoft.pidgoon.source;

import java.io.IOException;

public interface Position {

	String toString();

	Position advance() throws IOException;

	boolean isEOF();
}