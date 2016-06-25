package com.zarbosoft.pidgoon.bytes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.nodes.Not;
import com.zarbosoft.pidgoon.nodes.Reference;
import com.zarbosoft.pidgoon.nodes.Repeat;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.pidgoon.nodes.Union;
import com.zarbosoft.pidgoon.nodes.Wildcard;

public class GrammarFile {
	private final static Grammar grammar;
	static {
		grammar = new Grammar();
		grammar.add(
			"root", 
			new Repeat(
				new Sequence()
					.add(new Reference("interstitial"))
					.add(new Reference("rule"))
			)
		);
		grammar.add(
			"rule", 
			new Sequence()
				.add(new Reference("name"))
				.add(Terminal.fromChar(':'))
				.add(new Reference("interstitial"))
				.add(new Reference("expression"))
				.add(Terminal.fromChar(';').cut())
		);
		grammar.add(
			"identifier", 
			new Sequence()
				.add(new Repeat(
						new Union()
							.add(new Terminal(Range.closed((byte)'a', (byte)'z')))
							.add(new Terminal(Range.closed((byte)'A', (byte)'Z')))
							.add(new Terminal(Range.closed((byte)'0', (byte)'9')))
							.add(Terminal.fromChar('_')))
						.min(1))
				.add(new Reference("interstitial"))
		);
		grammar.add(
			"name", 
			new Reference("identifier")
		);
		grammar.add(
			"left_expression", 
			new Union()
				.add(new Sequence()
					.add(Terminal.fromChar('('))
					.add(new Reference("interstitial"))
					.add(new Reference("expression"))
					.add(Terminal.fromChar(')'))
					.add(new Reference("interstitial")))
				.add(new Reference("reference"))
				.add(new Reference("wildcard"))
				.add(new Reference("terminal"))
				.add(new Reference("drop"))
				.add(new Reference("not"))
				.add(new Reference("repone"))
				.add(new Reference("repmin"))
				.add(new Reference("rep"))
		);
		grammar.add(
			"expression", 
			new Union()
				.add(new Reference("left_expression"))
				.add(new Reference("sequence"))
				.add(new Reference("union"))
		);
		grammar.add(
			"reference", 
			new Reference("identifier")
		);
		grammar.add(
			"union", 
			new Sequence()
				.add(new Reference("left_expression"))
				.add(Terminal.fromChar('|'))
				.add(new Reference("interstitial"))
				.add(new Reference("expression"))
		);
		grammar.add(
			"sequence", 
			new Sequence()
				.add(new Reference("left_expression"))
				.add(new Reference("interstitial1"))
				.add(new Reference("expression"))
		);
		grammar.add(
			"terminal_escape", 
			new Sequence()
				.add(Terminal.fromChar('\\').drop())
				.add(new Wildcard())
		);
		grammar.add(
			"wildcard", 
			new Sequence()
				.add(Terminal.fromChar('.'))
				.add(new Reference("interstitial"))
		);
		grammar.add(
			"terminal", 
			new Union()
				.add(new Sequence()
					.add(Terminal.fromChar('[').drop())
					.add(new Repeat(
						new Union()
							.add(new Not(Terminal.fromChar('\\', ']')))
							.add(new Reference("terminal_escape"))
					).min(1))
					.add(Terminal.fromChar(']').drop())
					.add(new Reference("interstitial"))
				)
				.add(new Sequence()
					.add(Terminal.fromChar('\'').drop())
					.add(new Union()
						.add(new Not(Terminal.fromChar('\\', '\'')))
						.add(new Reference("terminal_escape"))
					)
					.add(Terminal.fromChar('\'').drop())
					.add(new Reference("interstitial"))
				)
		);
		grammar.add(
			"string", 
			new Sequence()
				.add(Terminal.fromChar('"').drop())
				.add(new Repeat(
					new Union()
						.add(new Not(Terminal.fromChar('\\', '"')))
						.add(new Sequence()
							.add(Terminal.fromChar('\\').drop())
							.add(new Wildcard()))
				).min(1))
				.add(Terminal.fromChar('"').drop())
				.add(new Reference("interstitial"))
		);
		grammar.add(
			"drop", 
			new Sequence()
				.add(Terminal.fromChar('#'))
				.add(new Reference("interstitial"))
				.add(new Reference("left_expression"))
		);
		grammar.add(
			"not", 
			new Sequence()
				.add(Terminal.fromChar('~'))
				.add(new Reference("interstitial"))
				.add(new Reference("left_expression"))
		);
		grammar.add(
			"repone", 
			new Sequence()
				.add(new Reference("left_expression"))
				.add(Terminal.fromChar('?'))
				.add(new Reference("interstitial"))
		);
		grammar.add(
			"repmin", 
			new Sequence()
				.add(new Reference("left_expression"))
				.add(Terminal.fromChar('+'))
				.add(new Reference("interstitial"))
		);
		grammar.add(
			"rep", 
			new Sequence()
				.add(new Reference("left_expression"))
				.add(Terminal.fromChar('*'))
				.add(new Reference("interstitial"))
		);
		grammar.add(
			"comment", 
			new Sequence()
				.add(Parse.stringSeq("//"))
				.add(new Not(new Reference("eol")))
				.add(new Reference("eol"))
		);
		grammar.add(
			"interstitial1", 
			new Repeat(
				new Union()
					.add(Terminal.fromChar(' ', '\t'))
					.add(new Reference("eol"))
					.add(new Reference("comment"))
				)
				.min(1)
				.drop()
		);
		grammar.add(
			"interstitial", 
			new Repeat(new Reference("interstitial1")).max(1)
		);
		grammar.add(
			"eol", 
			new Union()
				.add(new Sequence().add(new Terminal((byte)0x0D)).add(new Terminal((byte)0x0A)))
				.add(new Terminal((byte)0x0A))
				.drop()
		);
	}
	
	public static Parse<Grammar> parse() {
		return new Parse<Grammar>()
			.grammar(grammar)
			.node("root")
			.stack(() -> new Grammar())
			.callbacks(new ImmutableMap.Builder<String, Callback>()
				.put("rule", (store) -> {
					Node base = (Node) store.popStack();
					String name = (String) store.popStack();
					Grammar grammar = (Grammar) store.popStack();
					grammar.add(name, base);
					store.pushStack(grammar);
				})
				.put("name", (store) -> {
					store.pushStack(store.topData().toString());
				})
				.put("reference", (store) -> {
					store.pushStack(new Reference(store.topData().toString()));
				})
				.put("union", (store) -> {
					Node right = (Node) store.popStack();
					Node left = (Node) store.popStack();
					store.pushStack(new Union().add(left).add(right));
				})
				.put("sequence", (store) -> {
					Node right = (Node) store.popStack();
					Node left = (Node) store.popStack();
					store.pushStack(new Sequence().add(left).add(right));
				})
				.put("terminal_escape", (store) -> {
					// TODO \xNN escapes
					byte top = store.topData().dataFirst();
					if (top == (byte)'r') store.setData(new Clip((byte) '\r'));
					if (top == (byte)'n') store.setData(new Clip((byte) '\n'));
					if (top == (byte)'t') store.setData(new Clip((byte) '\t'));
				})
				.put("wildcard", (store) -> { store.pushStack(new Wildcard()); })
				.put("terminal", (store) -> {
					store.pushStack(new Terminal(store.topData().dataRender()));
				})
				.put("string", (store) -> {
					store.pushStack(Parse.byteSeq(store.topData().dataRender()));
				})
				.put("drop", (store) -> {
					store.pushStack(((Node)store.popStack()).drop());
				})
				.put("not", (store) -> {
					store.pushStack(new Not((Node)store.popStack()));
				})
				.put("repone", (store) -> {
					store.pushStack(new Repeat((Node)store.popStack()).max(1));
				})
				.put("repmin", (store) -> {
					store.pushStack(new Repeat((Node)store.popStack()).min(1));
				})
				.put("rep", (store) -> {
					store.pushStack(new Repeat((Node)store.popStack()));
				})
				.build()
			);
	}
	
	/*
	public static Grammar parse(InputStream stream, Map<String, Callback> callbacks) throws IOException {
		Grammar after = ((Grammar) g.build().node("root").stack(() -> new Grammar()).parse(stream)).build();
		System.out.println(String.format("Final grammar:\n%s\n", after);
		return after;
	}
	*/

}
