package com.zarbosoft.pidgoon.internal;

public interface Position {

	String toString();

	Position advance();

	boolean isEOF();

	long distance();
}