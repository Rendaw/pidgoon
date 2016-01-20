package com.zarbosoft.undepurseable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Range;
import com.zarbosoft.undepurseable.internal.BranchingStack;
import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.nodes.Capture;
import com.zarbosoft.undepurseable.nodes.Not;
import com.zarbosoft.undepurseable.nodes.Reference;
import com.zarbosoft.undepurseable.nodes.Repeat;
import com.zarbosoft.undepurseable.nodes.Sequence;
import com.zarbosoft.undepurseable.nodes.Terminal;
import com.zarbosoft.undepurseable.nodes.Union;
import com.zarbosoft.undepurseable.nodes.Wildcard;

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
				.add(Terminal.fromChar(';').cut()));
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
					store.pushStack(store.topData().toString());
				}));
		g.add(
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
		g.add(
			"expression", 
			new Union()
				.add(new Reference("left_expression"))
				.add(new Reference("sequence"))
				.add(new Reference("union")));
		g.add(
			"reference", 
			new Capture(
				new Reference("identifier"),
				(store) -> {
					store.pushStack(new Reference(store.topData().toString()));
				}));
		g.add(
			"union", 
			new Capture(
				new Sequence()
					.add(new Reference("left_expression"))
					.add(Terminal.fromChar('|'))
					.add(new Reference("interstitial"))
					.add(new Reference("expression")),
				(store) -> {
					Node right = (Node) store.popStack();
					Node left = (Node) store.popStack();
					store.pushStack(new Union().add(left).add(right));
				}));
		g.add(
			"sequence", 
			new Capture(
				new Sequence()
					.add(new Reference("left_expression"))
					.add(new Reference("interstitial1"))
					.add(new Reference("expression")),
				(store) -> {
					Node right = (Node) store.popStack();
					Node left = (Node) store.popStack();
					store.pushStack(new Sequence().add(left).add(right));
				}));
		g.add(
			"terminal_escape", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('\\').drop())
					.add(new Wildcard()),
				(store) -> {
					// TODO \xNN escapes
					byte top = store.topData().dataFirst();
					if (top == (byte)'r') store.setData(new Clip((byte) '\r'));
					if (top == (byte)'n') store.setData(new Clip((byte) '\n'));
					if (top == (byte)'t') store.setData(new Clip((byte) '\t'));
				}));
		g.add(
			"wildcard", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('.'))
					.add(new Reference("interstitial")),
				(store) -> { store.pushStack(new Wildcard()); }));
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
							.add(new Reference("terminal_escape"))
						)
						.add(Terminal.fromChar('\'').drop())
						.add(new Reference("interstitial"))
					),
				(store) -> {
					store.pushStack(new Terminal(store.topData().dataRender()));
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
								.add(new Wildcard()))
					).min(1))
					.add(Terminal.fromChar('"').drop())
					.add(new Reference("interstitial")),
				(store) -> {
					store.pushStack(Sequence.bytes(store.topData().dataRender()));
				}));
		g.add(
			"drop", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('#'))
					.add(new Reference("interstitial"))
					.add(new Reference("left_expression")),
				(store) -> {
					store.pushStack(((Node)store.popStack()).drop());
				}));
		g.add(
			"not", 
			new Capture(
				new Sequence()
					.add(Terminal.fromChar('~'))
					.add(new Reference("interstitial"))
					.add(new Reference("left_expression")),
				(store) -> {
					store.pushStack(new Not((Node)store.popStack()));
				}));
		g.add(
			"repone", 
			new Capture(
				new Sequence()
					.add(new Reference("left_expression"))
					.add(Terminal.fromChar('?'))
					.add(new Reference("interstitial")),
				(store) -> {
					store.pushStack(new Repeat((Node)store.popStack()).max(1));
				}));
		g.add(
			"repmin", 
			new Capture(
				new Sequence()
					.add(new Reference("left_expression"))
					.add(Terminal.fromChar('+'))
					.add(new Reference("interstitial")),
				(store) -> {
					store.pushStack(new Repeat((Node)store.popStack()).min(1));
				}));
		g.add(
			"rep", 
			new Capture(
				new Sequence()
					.add(new Reference("left_expression"))
					.add(Terminal.fromChar('*'))
					.add(new Reference("interstitial")),
				(store) -> {
					store.pushStack(new Repeat((Node)store.popStack()));
				}));
		g.add(
			"comment", 
			new Sequence()
				.add(Sequence.string("//"))
				.add(new Not(new Reference("eol")))
				.add(new Reference("eol")));
		g.add(
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
		g.add(
			"interstitial", 
			new Repeat(new Reference("interstitial1")).max(1)
		);
		g.add(
			"eol", 
			new Union()
				.add(new Sequence().add(new Terminal((byte)0x0D)).add(new Terminal((byte)0x0A)))
				.add(new Terminal((byte)0x0A))
				.drop());
		BranchingStack<Object> rules = g.parse("root", stream);
		Grammar after = new Grammar();
		while (rules != null) {
			Node base = (Node) rules.top();
			rules = rules.pop();
			String name = (String) rules.top();
			rules = rules.pop();
			after.add(
				name, 
				new Capture(
					base, 
					callbacks.getOrDefault(
						name, 
						new Callback() {
							@Override
							public void accept(Store store) {}
						}
					)
				)
			);
		}
		System.out.println(String.format("Final grammar:\n%s\n", after));
		return after;
	}

}
