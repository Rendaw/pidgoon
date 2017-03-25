package com.zarbosoft.pidgoon;

import com.google.common.collect.ImmutableMap;
import com.zarbosoft.pidgoon.bytes.ClipStore;
import com.zarbosoft.pidgoon.bytes.Grammar;
import com.zarbosoft.pidgoon.bytes.Parse;
import com.zarbosoft.pidgoon.bytes.Terminal;
import com.zarbosoft.pidgoon.internal.Callback;
import com.zarbosoft.pidgoon.nodes.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GrammarTest {
	@Test(expected = InvalidStream.class)
	public void testEOFFail() {
		final Grammar grammar = new Grammar();
		grammar.add("root", Terminal.fromChar('a'));
		new Parse<>().grammar(grammar).node("root").parse("b");
	}

	@Test
	public void testUnion() {
		final Grammar grammar = new Grammar();
		grammar.add(
				"root",
				new Union()
						.add(Parse.stringSeq("zarolous"))
						.add(new Union().add(Parse.stringSeq("zarolously").add(Parse.stringSeq("zindictive"))))
		);
		new Parse<>().grammar(grammar).node("root").parse("zarolous");
	}

	@Test
	public void testLeftRecurseStack() {
		final Grammar grammar = new Grammar();
		grammar.add(
				"one",
				new Union()
						.add(Terminal.fromChar('a'))
						.add(new Sequence().add(new Reference("one")).add(Terminal.fromChar('z')))
		);
		grammar.add("two", new Reference("one"));
		final Object result = new Parse<>()
				.grammar(grammar)
				.node("two")
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("two", s -> {
					return s.pushStack(s.topData().toString());
				}).build())
				.parse("azz");
		assertEquals("azz", result);
	}

	@Test
	public void testNot() {
		final Grammar grammar = new Grammar();
		grammar.add("one", new Sequence().add(new Not(Terminal.fromChar('a'))).add(Terminal.fromChar('z')));
		final Object result = new Parse<>()
				.grammar(grammar)
				.node("one")
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("one", s -> {
					return s.pushStack(s.topData().toString());
				}).build())
				.parse("qz");
		assertEquals("qz", result);
	}

	@Test
	public void testNot2() {
		final Grammar grammar = new Grammar();
		grammar.add("one", new Not(new Union().add(Terminal.fromChar('a')).add(Parse.stringSeq("zoq"))));
		new Parse<>().grammar(grammar).node("one").parse("zot");
	}

	@Test(expected = InvalidStream.class)
	public void testFailNot() {
		final Grammar grammar = new Grammar();
		grammar.add("one", new Sequence().add(new Not(Terminal.fromChar('a'))).add(Terminal.fromChar('z')));
		new Parse<>()
				.grammar(grammar)
				.node("one")
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("one", s -> {
					return s.pushStack(s.topData().toString());
				}).build())
				.parse("az");
	}

	@Test
	public void testSeqStorage() {
		final Grammar grammar = new Grammar();
		grammar.add(
				"one",
				new Sequence()
						.add(new Union().add(Terminal.fromChar('z')).add(Terminal.fromChar('z')))
						.add(Terminal.fromChar('a'))
		);
		final Object result = new Parse<>()
				.grammar(grammar)
				.node("one")
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("one", s -> {
					return s.pushStack(s.topData().toString());
				}).build())
				.parse("za");
		assertEquals("za", result);
	}

	@Test
	public void testSet() {
		final Grammar grammar = new Grammar();
		grammar.add(
				"one",
				new Sequence()
						.add(Terminal.fromChar('z'))
						.add(new Set().add(Terminal.fromChar('a')))
						.add(Terminal.fromChar('z'))
		);
		final Object result = new Parse<>()
				.grammar(grammar)
				.node("one")
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("one", s -> {
					return s.pushStack(s.topData().toString());
				}).build())
				.parse("zaz");
		assertEquals("zaz", result);
	}

	@Test
	public void testEmptySet() {
		final Grammar grammar = new Grammar();
		grammar.add("one", new Sequence().add(Terminal.fromChar('z')).add(new Set()).add(Terminal.fromChar('z')));
		final Object result = new Parse<>()
				.grammar(grammar)
				.node("one")
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("one", s -> {
					return s.pushStack(s.topData().toString());
				}).build())
				.parse("zz");
		assertEquals("zz", result);
	}
}
