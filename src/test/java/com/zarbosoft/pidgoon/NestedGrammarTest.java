package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.events.*;
import com.zarbosoft.pidgoon.nodes.Repeat;
import com.zarbosoft.pidgoon.nodes.Sequence;
import org.junit.Test;

import static com.zarbosoft.pidgoon.EventGrammarTest.EventA;
import static com.zarbosoft.pidgoon.EventGrammarTest.EventB;

public class NestedGrammarTest {
	@Test(expected = InvalidStream.class)
	public void testInnerFailure() {
		final Grammar inner = new Grammar();
		inner.add(
				"root",
				new Sequence().add(new MatchingEventTerminal(new EventA())).add(new MatchingEventTerminal(new EventB()))
		);
		final Grammar outer = new Grammar();
		outer.add("root", new Repeat(new Operator(new MatchingEventTerminal(new EventA()), s -> {
			EventStream<Object> e = s.stackTop();
			e = e.push(s.top(), "");
			return s.popStack().pushStack(e);
		})).min(2).max(2));
		EventStream<Object> outerParse = new Parse<>()
				.grammar(outer)
				.stack(() -> new Parse<>().grammar(inner).root("root").parse())
				.root("root")
				.parse();
		outerParse = outerParse.push(new EventA(), "");
		outerParse = outerParse.push(new EventA(), "");
		outerParse.finish();
	}
}
