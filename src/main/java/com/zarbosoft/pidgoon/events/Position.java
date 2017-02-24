package com.zarbosoft.pidgoon.events;

class Position implements com.zarbosoft.pidgoon.source.Position {

	public final Event event;
	public final String at;
	private final long distance;

	public Position(final Event event, final String at, final long distance) {
		this.event = event;
		this.at = at;
		this.distance = distance;
	}

	@Override
	public com.zarbosoft.pidgoon.source.Position advance() {
		return this;
	}

	@Override
	public boolean isEOF() {
		return false;
	}

	@Override
	public long distance() {
		return distance;
	}

	public Event get() {
		return event;
	}

	@Override
	public String toString() {
		return String.format("At %s, next is %s", at, event);
	}
}
