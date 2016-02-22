package com.zarbosoft.undepurseable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.junit.Test;

import com.zarbosoft.undepurseable.bytes.Capture;
import com.zarbosoft.undepurseable.bytes.Grammar;
import com.zarbosoft.undepurseable.bytes.GrammarParser;
import com.zarbosoft.undepurseable.bytes.Terminal;
import com.zarbosoft.undepurseable.bytes.internal.Callback;
import com.zarbosoft.undepurseable.nodes.Reference;
import com.zarbosoft.undepurseable.nodes.Sequence;
import com.zarbosoft.undepurseable.nodes.Union;

import junit.framework.TestCase;

public class AppTest extends TestCase
{
	@Test
	public void testUnion() throws IOException {
		Grammar grammar = new Grammar();
		grammar.add(
			"root", 
			new Union()
				.add(Grammar.stringSeq("zarolous"))
				.add(new Union()
					.add(Grammar.stringSeq("zarolously")
					.add(Grammar.stringSeq("zindictive")))));
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
					s.pushStack(s.topData().toString());
				}
			)
		);
		assertEquals("azz", grammar.parse("two", "azz").toString());
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
					s.pushStack(s.topData().toString());
				}
			)
		);
		assertEquals("za", grammar.parse("one", "za").toString());
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
				new ByteArrayInputStream("root : WS arrayBody;".getBytes(StandardCharsets.UTF_8)),
				new HashMap<String, Callback>()
			);
	}
	
	@Test
	public void testGrammarFile3() throws IOException {
		GrammarParser
			.parse(
				new ByteArrayInputStream("WS : #([ \\t\\n] | '*' ( ~[*\\\\] | '\\\\' . )* '*')*;".getBytes(StandardCharsets.UTF_8)),
				new HashMap<String, Callback>()
			);
	}

}
