package com.zarbosoft.pidgoon.events;

/**
 * Use this terminal if the events themselves define matching conditions.
 */
public class MatchingEventTerminal extends Terminal {
	private final MatchingEvent value;

	public MatchingEventTerminal(final MatchingEvent value) {
		this.value = value;
	}

	public String toString() {
		return String.format("'%s'", value.toString());
	}

	protected boolean matches(final Event event) {
		return value.matches((MatchingEvent) event);
	}
}
