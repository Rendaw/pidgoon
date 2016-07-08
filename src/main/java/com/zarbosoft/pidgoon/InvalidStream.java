package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.ParseContext;

import java.util.stream.Collectors;

public class InvalidStream extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;

	public InvalidStream(final ParseContext context, final Object position) {
		this(context, position, "Bad input");
	}

	public InvalidStream(final ParseContext context, final Object position, final String string) {
		super(String.format(
				"%s:\n%s\n%s",
				string,
				position,
				context.errors.stream()
						.map(l -> l.toString())
						.collect(Collectors.joining("\n"))
		));
	}
}
