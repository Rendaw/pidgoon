package com.zarbosoft.pidgoon.source;

public interface Position {

	String toString();

	Position advance();

	boolean isEOF();

	long distance();
}