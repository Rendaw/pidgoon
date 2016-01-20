package com.zarbosoft.undepurseable.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ParseContext {
	public ParseContext(GrammarPrivate grammar, InputStream stream) throws IOException {
		this.grammar = grammar;
		position = new Position(stream);
	}
	public GrammarPrivate grammar;
	public Position position;
	public Deque<TerminalReader> leaves = new ArrayDeque<>();
	public List<TerminalReader> errors = new ArrayList<>();
	public List<TerminalReader> outLeaves = new ArrayList<>();
	public List<BranchingStack<Object>> results = new ArrayList<>();
	public BranchingStack<Object> preferredResult = null;
}
