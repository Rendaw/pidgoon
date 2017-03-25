package com.zarbosoft.pidgoon.bytes;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class Position implements com.zarbosoft.pidgoon.internal.Position {
	InputStream stream;

	private int bufUsed = 0;
	byte buf[];
	int localOffset = 0;

	long absolute = 0;
	private long line = 0;
	private long column = 0;

	public Position(final InputStream stream) {
		this.stream = stream;
		buf = new byte[10 * 1024];
		try {
			bufUsed = stream.read(buf, 0, buf.length);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Position(final Position last) {
		stream = last.stream;
		if (last.localOffset + 1 < last.bufUsed) {
			bufUsed = last.bufUsed;
			buf = last.buf;
			localOffset = last.localOffset + 1;
		} else {
			buf = new byte[10 * 1024];
			try {
				bufUsed = stream.read(buf, 0, buf.length);
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
			localOffset = 0;
		}
		absolute = last.absolute + 1;
		if (bufUsed == -1) {
			line = last.line;
			column = last.column + 1;
		} else {
			final byte b = buf[localOffset];
			if (b == (byte) '\n') {
				line = last.line + 1;
				column = 0;
			} else {
				line = last.line;
				column = last.column + 1;
			}
		}
	}

	@Override
	public String toString() {
		final int windowStart = Math.max(Math.min(localOffset - 30, bufUsed - 60), 0);
		final int windowStop = Math.max(Math.min(bufUsed, windowStart + 60), 0);
		final String prefix = String.format("line %d, col %d: [", line, column);
		String window = new String(buf, StandardCharsets.US_ASCII).substring(windowStart, windowStop);
		window = window.replace("\n", ".");
		window = window.replace("\t", " ");
		return String.format("%s%s]\n%s%s",
				prefix,
				window,
				Strings.repeat(" ", prefix.length() + localOffset - windowStart),
				"^"
		);
	}

	@Override
	public Position advance() {
		if (bufUsed == -1) {
			return null;
		}
		return new Position(this);
	}

	@Override
	public boolean isEOF() {
		return bufUsed == -1;
	}

	@Override
	public long distance() {
		return absolute;
	}

	public Byte get() {
		return buf[localOffset];
	}

	public Clip getStoreData() {
		return new Clip(get());
	}
}
