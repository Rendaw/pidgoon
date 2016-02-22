package com.zarbosoft.undepurseable.internal;

import java.io.IOException;

public interface SourceStream {

	Position position() throws IOException;

}
