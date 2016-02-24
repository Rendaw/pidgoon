package com.zarbosoft.pidgoon.events;

public interface Event {

	default boolean matches(Event event) {
		return getClass() == event.getClass();
	}

}
