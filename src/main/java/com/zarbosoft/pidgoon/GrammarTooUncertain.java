package com.zarbosoft.pidgoon;

import java.util.stream.Collectors;

/**
 * There were too many branches when parsing.  This is configurable in the Parse.
 */
public class GrammarTooUncertain extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;
	public final Object position;
	public final ParseContext context;

	public GrammarTooUncertain(final ParseContext context, final Object position) {
		this.context = context;
		this.position = position;
	}

	@Override
	public String toString() {
		return String.format(
				"Grammar too uncertain (%d possible next states) at:\n%s\n%s",
				context.leaves.size(),
				position,
				context.leaves.stream().map(l -> l.toString()).collect(Collectors.joining("\n"))
		);
	}
}
