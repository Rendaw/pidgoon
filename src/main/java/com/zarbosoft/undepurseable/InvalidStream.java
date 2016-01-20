package com.zarbosoft.undepurseable;

import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.internal.ParseContext;

public class InvalidStream extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;
	private ParseContext context;

	public InvalidStream(ParseContext context) {
		super("Bad input");
		this.context = context;
	}
	
	public InvalidStream(ParseContext context, String string) {
		super(string);
		this.context = context;
	}

	@Override
	public String toString() {
		return String.format(
			"%s:\n%s\n%s", 
			super.toString(),
			context.position, 
			context.errors.stream()
				.map(l -> l.toString())
				.collect(Collectors.joining("\n")));
	}
}
