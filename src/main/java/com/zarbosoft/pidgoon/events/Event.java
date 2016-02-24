package com.zarbosoft.pidgoon.events;

public interface Event {

	boolean matches(Event event);

}
