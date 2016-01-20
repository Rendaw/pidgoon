package com.zarbosoft.undepurseable;

import java.util.stream.Collectors;

import com.zarbosoft.undepurseable.internal.Position;

public class GrammarTooAmbiguous extends RuntimeException {
	private static final long serialVersionUID = -1049832348704101941L;
	public Position position;

	public GrammarTooAmbiguous(Position position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		return String.format(
			"Grammar too ambiguous (%d simultaneous next terminals) at:\n%s\n%s", 
			position.getLeaves().size(),
			position, 
			position.getLeaves().stream()
				.map(l -> l.toString())
				.collect(Collectors.joining("\n")));
	}
}
