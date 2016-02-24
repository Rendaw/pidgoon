package com.zarbosoft.pidgoon.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zarbosoft.pidgoon.Grammar;
import com.zarbosoft.pidgoon.source.Position;

public class ParseContext {
	public ParseContext(Grammar grammar, Position initialPosition, Map<String, Object> callbacks) {
		this.grammar = grammar;
		this.callbacks = callbacks;
		if (callbacks == null) this.callbacks = new HashMap<>();
		position = initialPosition;
	}
	public Grammar grammar;
	public Map<String, Object> callbacks;
	public Position position;
	public Deque<TerminalReader> leaves = new ArrayDeque<>();
	public List<TerminalReader> errors = new ArrayList<>();
	public List<TerminalReader> outLeaves = new ArrayList<>();
	public List<Object> results = new ArrayList<>();
	public Object preferredResult = null;
}
