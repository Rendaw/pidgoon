package com.zarbosoft.pidgoon.events;

import java.io.IOException;

class Position implements com.zarbosoft.pidgoon.source.Position {

	public Event event;

	@Override
	public com.zarbosoft.pidgoon.source.Position advance() throws IOException {
		return this;
	}

	@Override
	public boolean isEOF() {
		return false;
	}

	public Event get() {
		return event;
	}

}
