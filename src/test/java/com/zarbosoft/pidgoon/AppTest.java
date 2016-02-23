package com.zarbosoft.pidgoon;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.zarbosoft.pidgoon.bytes.Callback;
import com.zarbosoft.pidgoon.bytes.Grammar;
import com.zarbosoft.pidgoon.bytes.GrammarFile;
import com.zarbosoft.pidgoon.bytes.Parse;
import com.zarbosoft.pidgoon.bytes.Terminal;
import com.zarbosoft.pidgoon.nodes.Reference;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.pidgoon.nodes.Union;

import junit.framework.TestCase;

public class AppTest extends TestCase
{
	@Test
	public void testUnion() throws IOException {
		Grammar grammar = new Grammar();
		grammar.add(
			"root", 
			new Union()
				.add(Parse.stringSeq("zarolous"))
				.add(new Union()
					.add(Parse.stringSeq("zarolously")
					.add(Parse.stringSeq("zindictive")))));
		new Parse<>()
			.grammar(grammar)
			.node("root")
			.parse("zarolous");
	}
	
	@Test
	public void testLeftRecurseStack() throws IOException {
		Grammar grammar = new Grammar();
		grammar.add(
			"one", 
			new Union()
				.add(Terminal.fromChar('a'))
				.add(new Sequence()
						.add(new Reference("one"))
						.add(Terminal.fromChar('z'))
				)
		);
		grammar.add(
			"two", 
			new Reference("one")
		);
		Object result = new Parse<>()
			.grammar(grammar)
			.node("two")
			.callbacks(new ImmutableMap.Builder<String, Callback>()
				.put("two", s -> {
					s.pushStack(s.topData().toString());
				})
				.build()
			)
			.parse("azz");
		assertEquals("azz", result);
	}

	@Test
	public void testSeqStorage() throws IOException {
		Grammar grammar = new Grammar();
		grammar.add(
			"one", 
			new Sequence()
				.add(new Union()
					.add(Terminal.fromChar('z'))
					.add(Terminal.fromChar('z'))
				)
				.add(Terminal.fromChar('a'))
		);
		Object result = new Parse<>()
			.grammar(grammar)
			.node("one")
			.callbacks(new ImmutableMap.Builder<String, Callback>()
				.put("one", s -> {
					s.pushStack(s.topData().toString());
				})
				.build()
			)
			.parse("za");
		assertEquals("za", result);
	}
	
	@Test
	public void testGrammarFile() throws IOException {
		new Parse<>()
			.grammar(GrammarFile.parse()
				.parse("rule : 'h' 'i';\n\n")
			)
			.node("rule")
			.parse("hi"); 
	}

	@Test
	public void testGrammarFile2() throws IOException {
		GrammarFile
			.parse()
			.parse("root : WS arrayBody;");
	}
	
	@Test
	public void testGrammarFile3() throws IOException {
		GrammarFile
			.parse()
			.parse("WS : #([ \\t\\n] | '*' ( ~[*\\\\] | '\\\\' . )* '*')*;");
	}

}
