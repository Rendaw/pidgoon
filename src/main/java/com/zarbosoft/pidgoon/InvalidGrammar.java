package com.zarbosoft.pidgoon;

/**
 * An error in the grammar definition.
 */
public class InvalidGrammar extends Error {
	private static final long serialVersionUID = -3566313529549278458L;

	public InvalidGrammar(final String text) {
		super(text);
	}
}
