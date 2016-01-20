package com.zarbosoft.undepurseable;

import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.internal.ParseContext;

public class GrammarTooAmbiguous extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;
	private ParseContext context;

	public GrammarTooAmbiguous(ParseContext context) {
		this.context = context;
	}
	
	@Override
	public String toString() {
		return String.format(
			"Grammar too ambiguous (%d simultaneous next terminals) at:\n%s\n%s", 
			context.outLeaves.size(),
			context.position, 
			context.outLeaves.stream()
				.map(l -> l.toString())
				.collect(Collectors.joining("\n")));
	}
}
