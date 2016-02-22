package com.zarbosoft.undepurseable.source;

import java.io.IOException;

import com.zarbosoft.undepurseable.internal.Position;

public interface SourceStream {

	Position position() throws IOException;

}
