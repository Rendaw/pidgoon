package com.zarbosoft.pidgoon;

public class AbortParse extends RuntimeException {
	public AbortParse(final String s) {
		super(s);
	}

	public AbortParse() {
	}

	public AbortParse(final Throwable source) {
		super(source);
	}
}
