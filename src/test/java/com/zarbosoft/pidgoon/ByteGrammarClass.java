package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.bytes.Grammar;
import com.zarbosoft.pidgoon.bytes.Parse;
import com.zarbosoft.pidgoon.bytes.Position;
import com.zarbosoft.rendaw.common.Pair;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ByteGrammarClass {
	private static final Grammar branchlessGrammar;

	static {
		branchlessGrammar = new Grammar();
		branchlessGrammar.add("root", Grammar.stringSequence("talking"));
	}

	@Test
	public void longestMatch0() {
		final Pair<ParseContext, Position> results = new Parse<>()
				.grammar(branchlessGrammar)
				.longestMatchFromStart(new ByteArrayInputStream("quiz".getBytes(StandardCharsets.UTF_8)));
		assertThat(results.second.distance(), equalTo(0L));
	}

	@Test
	public void longestMatchMid() {
		final Pair<ParseContext, Position> results = new Parse<>()
				.grammar(branchlessGrammar)
				.longestMatchFromStart(new ByteArrayInputStream("t".getBytes(StandardCharsets.UTF_8)));
		assertThat(results.second.distance(), equalTo(1L));
	}

	@Test
	public void longestMatchMid2() {
		final Pair<ParseContext, Position> results = new Parse<>()
				.grammar(branchlessGrammar)
				.longestMatchFromStart(new ByteArrayInputStream("talkin".getBytes(StandardCharsets.UTF_8)));
		assertThat(results.second.distance(), equalTo(6L));
	}

	@Test
	public void longestMatchFull() {
		final Pair<ParseContext, Position> results = new Parse<>()
				.grammar(branchlessGrammar)
				.longestMatchFromStart(new ByteArrayInputStream("talking".getBytes(StandardCharsets.UTF_8)));
		assertThat(results.second.distance(), equalTo(7L));
	}

	@Test
	public void longestMatchOver() {
		final Pair<ParseContext, Position> results = new Parse<>()
				.grammar(branchlessGrammar)
				.longestMatchFromStart(new ByteArrayInputStream("talkinger".getBytes(StandardCharsets.UTF_8)));
		assertThat(results.second.distance(), equalTo(7L));
	}
}
