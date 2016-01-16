package com.zarbosoft.undepurseable;

import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.internal.Position;

public class InvalidStream extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;
	public Position position;

	public InvalidStream(Position position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		return String.format(
			"Bad input at:\n%s\n%s", 
			position, 
			position.errors.stream()
				.collect(Collectors.joining("\n")));
	}
}
