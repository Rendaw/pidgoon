package com.zarbosoft.pidgoon;

import com.google.common.collect.ImmutableMap;
import com.zarbosoft.pidgoon.bytes.ClipStore;
import com.zarbosoft.pidgoon.bytes.GrammarFile;
import com.zarbosoft.pidgoon.bytes.Parse;
import com.zarbosoft.pidgoon.internal.Callback;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GrammarFileTest {
	@Test
	public void testClasses() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : [a\\n]+ ;\n\n")).root("rule").parse("a\n");
	}

	@Test(expected = InvalidStream.class)
	public void testClassesFail() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : [a\\n]+ ;\n\n")).root("rule").parse("n");
	}

	@Test
	public void testEscapes() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : '\\\\' 'a';")).root("rule").parse("\\a");
	}

	@Test(expected = InvalidStream.class)
	public void testEscapesFail() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : '\\\\' 'a';")).root("rule").parse("a");
	}

	@Test
	public void testEscapes2() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : '\\'' 'a';")).root("rule").parse("'a");
	}

	@Test(expected = InvalidStream.class)
	public void testEscapes2Fail() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : '\\'' 'a';")).root("rule").parse("a");
	}

	@Test
	public void testGrammarFile() {
		new Parse<>().grammar(GrammarFile.parse().parse("rule : 'h' 'i';\n\n")).root("rule").parse("hi");
	}

	@Test
	public void testGrammarFile2() {
		GrammarFile.parse().parse("root : WS arrayBody;");
	}

	@Test
	public void testGrammarFile3() {
		GrammarFile.parse().parse("WS : #([ \\t\\n] | '*' ( ~[*\\\\] | '\\\\' . )* '*')*;");
	}

	@Test
	public void testNot() {
		final String result = new Parse<String>()
				.grammar(GrammarFile.parse().parse("root : ~[a];"))
				.callbacks(ImmutableMap.<Object, Callback<ClipStore>>builder()
						.put("root", s -> s.pushStack(s.topData().toString()))
						.build())
				.root("root")
				.parse("z");
		assertEquals("z", result);
	}

	@Test
	public void testRepMin() {
		new Parse<>().grammar(GrammarFile.parse().parse("root : [a]+;")).root("root").parse("aa");
	}

	@Test
	public void testNotBinding1() {
		new Parse<>().grammar(GrammarFile.parse().parse("root : ~[a]+;")).root("root").parse("z");
	}

	@Test
	public void testNotBinding2() {
		new Parse<>().grammar(GrammarFile.parse().parse("root : (~[a])+;")).root("root").parse("zg");
	}

	@Test
	public void testNotBinding3() {
		new Parse<>().grammar(GrammarFile.parse().parse("root : ~([a]+);")).root("root").parse("z");
	}
}
