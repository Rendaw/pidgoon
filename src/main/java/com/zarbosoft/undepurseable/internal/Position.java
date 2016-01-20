package com.zarbosoft.undepurseable.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

public class Position {
	public GrammarPrivate grammar;
	InputStream stream;
	
	private int bufUsed = 0;
	byte buf[];
	int localOffset = 0;
	
	long absolute = 0;
	private long line = 0;
	private long column = 0;

	public List<TerminalContext> errors = new ArrayList<>();
	protected List<Store> results = new ArrayList<>();
	private List<TerminalContext> leaves = new ArrayList<>();

	public Position(GrammarPrivate grammar, InputStream stream) throws IOException {
		this.grammar = grammar;
		this.stream = stream;
		buf = new byte[10 * 1024];
		bufUsed = stream.read(buf, 0, buf.length);
	}

	private Position(Position last) throws IOException {
		this.grammar = last.grammar;
		stream = last.stream;
		if (last.localOffset + 1 < last.bufUsed) {
			bufUsed = last.bufUsed;
			buf = last.buf;
			localOffset = last.localOffset + 1;
		} else {
			buf = new byte[10 * 1024];
			bufUsed = stream.read(buf, 0, buf.length);
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
		int windowStop = Math.min(bufUsed, windowStart + 60);
		String prefix = String.format("line %d, col %d: [", line, column);
		return String.format(
			"%s%s]\n%s%s", 
			prefix, new String(buf, StandardCharsets.US_ASCII).substring(windowStart, windowStop).replace("\n", "."),
			Strings.repeat(" ", prefix.length() + localOffset - windowStart), "^");
	}

	public Position advance() throws IOException {
		if (bufUsed == -1) {
			return null;
		}
		return new Position(this);
	}

	public boolean isEOF() {
		return bufUsed == -1;
	}

	public Byte get() {
		return buf[localOffset];
	}
	
	public void addLeaf(TerminalContext leaf) {
		leaves.add(leaf);
	}

	public long getAbsolute() {
		return absolute;
	}

	public List<TerminalContext> takeLeaves() {
		List<TerminalContext> out = leaves;
		leaves = new ArrayList<>();
		return out;
	}

	public List<TerminalContext> getLeaves() {
		return leaves;
	}
}
