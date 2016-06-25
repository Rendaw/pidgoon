package com.zarbosoft.pidgoon.events;

class Position implements com.zarbosoft.pidgoon.source.Position {

	public Event event;
	public String at;

	@Override
	public com.zarbosoft.pidgoon.source.Position advance() {
		return this;
	}

	@Override
	public boolean isEOF() {
		return false;
	}

	public Event get() {
		return event;
	}

	@Override
	public String toString() {
		return String.format("At %s, next is %s", at, event);
	}
}
