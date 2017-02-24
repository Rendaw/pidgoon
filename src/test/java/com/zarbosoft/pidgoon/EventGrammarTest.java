package com.zarbosoft.pidgoon;

import com.zarbosoft.pidgoon.events.*;
import com.zarbosoft.pidgoon.events.Grammar;
import com.zarbosoft.pidgoon.nodes.Sequence;
import org.junit.Test;

public class EventGrammarTest {
	public static class EventA implements Event {
	}

	public static class EventB implements Event {
	}

	@Test(expected = InvalidStream.class)
	public void testEventGrammarFailure() {
		final Grammar inner = new Grammar();
		inner.add("root", new Sequence().add(new Terminal(new EventA())).add(new Terminal(new EventB())));
		EventStream<Object> parse = new Parse<>().grammar(inner).node("root").parse();
		parse = parse.push(new EventB(), "");
		parse = parse.push(new EventB(), "");
		parse.finish();
	}

	@Test(expected = InvalidStream.class)
	public void testEventGrammarEOFFailure() {
		final Grammar inner = new Grammar();
		inner.add("root", new Sequence().add(new Terminal(new EventA())).add(new Terminal(new EventB())));
		EventStream<Object> parse = new Parse<>().grammar(inner).node("root").parse();
		parse = parse.push(new EventA(), "");
		parse = parse.push(new EventA(), "");
		parse.finish();
	}

	@Test
	public void testEventGrammarPass() {
		final Grammar inner = new Grammar();
		inner.add("root", new Sequence().add(new Terminal(new EventA())).add(new Terminal(new EventB())));
		EventStream<Object> parse = new Parse<>().grammar(inner).node("root").parse();
		parse = parse.push(new EventA(), "");
		parse = parse.push(new EventB(), "");
	}

}
