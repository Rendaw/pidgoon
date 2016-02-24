package com.zarbosoft.pidgoon.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import com.google.common.base.Strings;

public class Position implements com.zarbosoft.pidgoon.source.Position {
	InputStream stream;
	
	private int bufUsed = 0;
	byte buf[];
	int localOffset = 0;
	
	long absolute = 0;
	private long line = 0;
	private long column = 0;

	public Position(InputStream stream) {
		this.stream = stream;
		buf = new byte[10 * 1024];
		try {
			bufUsed = stream.read(buf, 0, buf.length);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Position(Position last) {
		stream = last.stream;
		if (last.localOffset + 1 < last.bufUsed) {
			bufUsed = last.bufUsed;
			buf = last.buf;
			localOffset = last.localOffset + 1;
		} else {
			buf = new byte[10 * 1024];
			try {
				bufUsed = stream.read(buf, 0, buf.length);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			localOffset = 0;
		}
		absolute = last.absolute + 1;
		if (bufUsed == -1) {
			line = last.line;
			column = last.column + 1;
		} else {
			byte b = buf[localOffset];
			if (b == (byte) '\n') {
				line = last.line + 1;
				column = 0;
			} 
			else {
				line = last.line;
				column = last.column + 1;
			}
		}
	}

	@Override
	public String toString() {
		int windowStart = Math.max(Math.min(localOffset - 30, bufUsed - 60), 0);
		int windowStop = Math.max(Math.min(bufUsed, windowStart + 60), 0);
		String prefix = String.format("line %d, col %d: [", line, column);
		return String.format(
			"%s%s]\n%s%s", 
			prefix, new String(buf, StandardCharsets.US_ASCII).substring(windowStart, windowStop).replace("\n", "."),
			Strings.repeat(" ", prefix.length() + localOffset - windowStart), "^");
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

	public Byte get() {
		return buf[localOffset];
	}

	public Clip getStoreData() {
		return new Clip(get());
	}
}
