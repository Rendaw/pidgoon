package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.source.Position;

public abstract class State {
	public abstract void parse(ParseContext step, Position position);

}
