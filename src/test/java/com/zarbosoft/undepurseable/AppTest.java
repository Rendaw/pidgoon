package com.zarbosoft.undepurseable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.junit.Test;

import com.zarbosoft.undepurseable.nodes.Capture;
import com.zarbosoft.undepurseable.nodes.Reference;
import com.zarbosoft.undepurseable.nodes.Sequence;
import com.zarbosoft.undepurseable.nodes.Terminal;
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
					s.stack.push(s.dataString());
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
			new Sequence()
				.add(new Union()
					.add(Terminal.fromChar('z'))
					.add(Terminal.fromChar('z'))
				)
				.add(Terminal.fromChar('a'))
		);
		assertEquals("za", grammar.parse("one", "za").getLast());
	}
	
	@Test
	public void testGrammarFile() throws IOException {
		GrammarParser
			.parse(
				new ByteArrayInputStream("rule : 'h' 'i';\n\n".getBytes(StandardCharsets.UTF_8)),
				new HashMap<String, Callback>())
			.parse("rule", "hi"); 
		
	}
}
