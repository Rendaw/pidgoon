package com.zarbosoft.undepurseable.bytes.internal;

import java.io.IOException;
import java.io.InputStream;

import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.SourceStream;

public class Stream implements SourceStream {

	private InputStream stream;

	public Stream(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public Position position() throws IOException {
		return new com.zarbosoft.undepurseable.bytes.internal.Position(stream);
	}

}
