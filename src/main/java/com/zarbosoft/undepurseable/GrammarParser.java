package com.zarbosoft.undepurseable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.Map;

import com.google.common.collect.Range;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.nodes.Capture;
import com.zarbosoft.undepurseable.nodes.Not;
import com.zarbosoft.undepurseable.nodes.Reference;
import com.zarbosoft.undepurseable.nodes.Repeat;
import com.zarbosoft.undepurseable.nodes.Sequence;
import com.zarbosoft.undepurseable.nodes.Terminal;
import com.zarbosoft.undepurseable.nodes.Union;

public class GrammarParser {
	public static Grammar parse(InputStream stream, Map<String, Callback> callbacks) throws IOException {
		Grammar g = new Grammar();
		g.add(
			"root", 
			new Repeat(
				new Sequence()
					.add(new Reference("interstitial"))
					.add(new Reference("rule"))));
		g.add(
			"rule", 
			new Sequence()
				.add(new Reference("name"))
				.add(Terminal.fromChar(':'))
				.add(new Reference("interstitial"))
				.add(new Reference("expression"))
				.add(Terminal.fromChar(';')));
		g.add(
			"identifier", 
			new Sequence()
				.add(new Repeat(
						new Union()
							.add(new Terminal(Range.closed((byte)'a', (byte)'z')))
							.add(new Terminal(Range.closed((byte)'A', (byte)'Z')))
							.add(new Terminal(Range.closed((byte)'0', (byte)'9')))
							.add(Terminal.fromChar('_')))
						.min(1))
				.add(new Reference("interstitial")));
		g.add(
			"name", 
			new Capture(
				new Reference("identifier"),
				(store) -> {
					store.stack.addLast(store.dataString());
				}));
		g.add(
			"expression", 
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
				.add(new Reference("sequence"))
				.add(new Reference("union")));
		g.add(
			"reference", 
			new Capture(
				new Reference("identifier"),
				(store) -> {
					store.stack.addLast(((Node)store.stack.removeLast()).drop());
				}));
		g.add(
			"union", 
			new Capture(
				new Sequence()
					.add(new Reference("expression"))
					.add(Terminal.fromChar('|'))
					.add(new Reference("interstitial"))
					.add(new Reference("expression")),
				(store) -> {
					Node left = (Node) store.stack.removeLast();
					Node right = (Node) store.stack.removeLast();
					store.stack.addLast(new Union().add(left).add(right));
				}));
		g.add(
			"sequence", 
			new Capture(
				new Sequence()
					.add(new Reference("expression"))
					.add(new Reference("expression")),
				(store) -> {
					Node left = (Node) store.stack.removeLast();
					Node right = (Node) store.stack.removeLast();
					store.stack.addLast(new Sequence().add(left).add(right));
				}));
		g.add(
			"terminal_escape", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('\\').drop())
					.add(new Terminal()),
				(store) -> {
					// TODO \xNN escapes
					byte top = store.dataFirst();
					if (top == (byte)'r') top = '\r';
					if (top == (byte)'n') top = '\n';
					if (top == (byte)'t') top = '\t';
					store.stack.addLast(new Terminal(Range.closedOpen(top, (byte)(top + 1))));
				}));
		g.add(
			"wildcard", 
			new Capture(
				Terminal.fromChar('.'),
				(store) -> { store.stack.addLast(new Terminal()); }));
		g.add(
			"terminal", 
			new Capture(
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
							.add(new Sequence()
								.add(Terminal.fromChar('\\').drop())
								.add(new Reference("terminal_escape"))
							)
						)
						.add(Terminal.fromChar('\'').drop())
						.add(new Reference("interstitial"))
					),
				(store) -> {
					System.out.println(String.format("Got terminal [%s]", store.dataString()));
					store.stack.addLast(new Terminal(store.dataRender()));
				}));
		g.add(
			"string", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('"').drop())
					.add(new Repeat(
						new Union()
							.add(new Not(Terminal.fromChar('\\', '"')))
							.add(new Sequence()
								.add(Terminal.fromChar('\\').drop())
								.add(new Terminal()))
					).min(1))
					.add(Terminal.fromChar('"').drop())
					.add(new Reference("interstitial")),
				(store) -> {
					store.stack.addLast(Sequence.bytes(store.dataRender()));
				}));
		g.add(
			"drop", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('#'))
					.add(new Reference("interstitial"))
					.add(new Reference("expression")),
				(store) -> {
					store.stack.addLast(((Node)store.stack.removeLast()).drop());
				}));
		g.add(
			"not", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('~'))
					.add(new Reference("interstitial"))
					.add(new Reference("expression")),
				(store) -> {
					store.stack.addLast(new Not((Node)store.stack.removeLast()));
				}));
		g.add(
			"repone", 
			new Capture(
				new Sequence()
					.add(new Reference("expression"))
					.add(Terminal.fromChar('?'))
					.add(new Reference("interstitial")),
				(store) -> {
					store.stack.addLast(new Repeat((Node)store.stack.removeLast()).max(1));
				}));
		g.add(
			"repmin", 
			new Capture(
				new Sequence()
					.add(new Reference("expression"))
					.add(Terminal.fromChar('+'))
					.add(new Reference("interstitial")),
				(store) -> {
					store.stack.addLast(new Repeat((Node)store.stack.removeLast()).min(1));
				}));
		g.add(
			"rep", 
			new Capture(
				new Sequence()
					.add(new Reference("expression"))
					.add(Terminal.fromChar('*'))
					.add(new Reference("interstitial")),
				(store) -> {
					store.stack.addLast(new Repeat((Node)store.stack.removeLast()));
				}));
		g.add(
			"comment", 
			new Sequence()
				.add(Sequence.string("//"))
				.add(new Not(new Reference("eol")))
				.add(new Reference("eol")));
		g.add(
			"interstitial", 
			new Repeat(
				new Union()
					.add(Terminal.fromChar(' ', '\t'))
					.add(new Reference("eol"))
					.add(new Reference("comment")))
				.drop());
		g.add(
			"eol", 
			new Union()
				.add(new Sequence().add(new Terminal((byte)0x0D)).add(new Terminal((byte)0x0A)))
				.add(new Terminal((byte)0x0A))
				.drop());
		Deque<Object> rules = g.parse("root", stream);
		Grammar after = new Grammar();
		while (!rules.isEmpty()) {
			Node base = (Node) rules.removeLast();
			String name = (String) rules.removeLast();
			after.add(name, new Capture(base, callbacks.get(name)));
		}
		System.out.println(String.format("Final grammar:\n%s\n", after));
		return after;
	}

}
