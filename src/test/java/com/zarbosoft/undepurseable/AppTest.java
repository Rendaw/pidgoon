package com.zarbosoft.undepurseable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.junit.Test;

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
				.add(Sequence.string("zarolous"))
				.add(new Union()
					.add(Sequence.string("zarolously")
					.add(Sequence.string("zindictive")))));
		grammar.parse("root", "zarolous");
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
