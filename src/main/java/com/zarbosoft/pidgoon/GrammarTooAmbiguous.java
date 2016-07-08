package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.ParseContext;

import java.util.stream.Collectors;

public class GrammarTooAmbiguous extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;
	private final Object position;
	private final ParseContext context;

	public GrammarTooAmbiguous(final ParseContext context, final Object position) {
		this.context = context;
		this.position = position;
	}

	@Override
	public String toString() {
		return String.format(
				"Grammar too ambiguous (%d simultaneous next terminals) at:\n%s\n%s",
				context.outLeaves.size(),
				position,
				context.outLeaves.stream()
						.map(l -> l.toString())
						.collect(Collectors.joining("\n"))
		);
	}
}
