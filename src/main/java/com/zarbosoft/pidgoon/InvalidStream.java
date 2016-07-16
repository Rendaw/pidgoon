package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.ParseContext;

import java.util.stream.Collectors;

public class InvalidStream extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;

	public InvalidStream(final ParseContext context) {
		this(context, "Bad input");
	}

	public InvalidStream(final ParseContext context, final String string) {
		super(String.format("%s\n\n%s", string, context.errorHistory.stream().map(s -> String.format(
				"%s\n%s\n",
				s.first.toString(),
				s.second.stream().map(l -> l.toString()).collect(Collectors.joining("\n"))
		)).collect(Collectors.joining("\n"))));
	}
}
