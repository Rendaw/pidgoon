package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.ParseContext;

public abstract class State {
	public abstract void parse(ParseContext step, Position position);

}
