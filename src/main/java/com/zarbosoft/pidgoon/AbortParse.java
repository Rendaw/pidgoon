package com.zarbosoft.pidgoon;

/**
 * Raise in an operator to cause a branch to fail.  Other branches will continue to parse.
 */
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
