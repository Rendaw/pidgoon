package com.zarbosoft.undepurseable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.junit.Test;

import junit.framework.TestCase;

public class AppTest extends TestCase
{
	/*
	@Test
	public void testUnion() throws IOException {
		Grammar grammar = new Grammar();
		grammar.add(
			"root", 
			new Union()
				.add(Sequence.string("zarolous"))
				.add(new Union()
					.add(Sequence.string("zarolously")
					.add(Sequence.string("zindictive")))));
		grammar.parse("root", "zarolous");
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
			new Capture(
				new Reference("one"),
				s -> {
					s.stack.push(s.topData().toString());
				}
			)
		);
		assertEquals("azz", grammar.parse("two", "azz").getLast());
	}

	@Test
	public void testSeqStorage() throws IOException {
		Grammar grammar = new Grammar();
		grammar.add(
			"one", 
			new Capture(
				new Sequence()
					.add(new Union()
						.add(Terminal.fromChar('z'))
						.add(Terminal.fromChar('z'))
					)
					.add(Terminal.fromChar('a')),
				s -> {
					s.stack.push(s.topData().toString());
				}
			)
		);
		assertEquals("za", grammar.parse("one", "za").getLast());
	}
	
	@Test
	public void testGrammarFile() throws IOException {
		GrammarParser
			.parse(
				new ByteArrayInputStream("rule : 'h' 'i';\n\n".getBytes(StandardCharsets.UTF_8)),
				new HashMap<String, Callback>()
			)
			.parse("rule", "hi"); 
		
	}

	@Test
	public void testGrammarFile2() throws IOException {
		GrammarParser
			.parse(
				//new ByteArrayInputStream("root : WS arrayBody EOF;".getBytes(StandardCharsets.UTF_8)),
				new ByteArrayInputStream("root : WS arrayBody;".getBytes(StandardCharsets.UTF_8)),
				new HashMap<String, Callback>()
			);
	}
	*/
	
	@Test
	public void testGrammarFile3() throws IOException {
		GrammarParser
			.parse(
				new ByteArrayInputStream("WS : #([ \\t\\n] | '*' ( ~[*\\\\] | '\\\\' . )* '*')*;".getBytes(StandardCharsets.UTF_8)),
				//new ByteArrayInputStream("WS : #(c | a b )*;".getBytes(StandardCharsets.UTF_8)),
				new HashMap<String, Callback>()
			);
	}

}
