package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.internal.Position;

import java.util.stream.Collectors;

/**
 * The grammar couldn't match the stream (all branches failed before the stream ended).
 */
public class InvalidStream extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;

	public InvalidStream(final ParseContext context, final Position position) {
		this(context, position.toString());
	}

	public InvalidStream(final ParseContext context, final String string) {
		super(String.format(
				"%s\n" + "\n" + "History\n" + "=======\n" + "\n" + "%s",
				string,
				context.errorHistory.stream().map(s -> String.format(
						"%s\nThe following failed to match:\n%s\n",
						s.first.toString(),
						s.second.stream().map(l -> l.toString()).collect(Collectors.joining("\n"))
				)).collect(Collectors.joining("\n"))
		));
	}
}
