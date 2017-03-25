package com.zarbosoft.pidgoon.bytes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.internal.Callback;
import com.zarbosoft.pidgoon.internal.Helper;
import com.zarbosoft.pidgoon.nodes.*;
import com.zarbosoft.rendaw.common.Pair;

public class GrammarFile {
	private static Grammar grammar;

	private static void buildGrammar() {
		grammar = new Grammar();
		grammar.add(
				"root",
				new Sequence()
						.add(new Reference("interstitial"))
						.add(new Repeat(new Sequence().add(new Reference("rule")).add(new Reference("interstitial"))))
		);
		grammar.add(
				"rule",
				new Sequence()
						.add(new Reference("name"))
						.add(Terminal.fromChar(':'))
						.add(new Reference("interstitial"))
						.add(new Reference("expression"))
						.add(new Cut(Terminal.fromChar(';')))
		);
		grammar.add(
				"identifier",
				new Sequence()
						.add(new Repeat(new Union()
								.add(new Terminal(Range.closed((byte) 'a', (byte) 'z')))
								.add(new Terminal(Range.closed((byte) 'A', (byte) 'Z')))
								.add(new Terminal(Range.closed((byte) '0', (byte) '9')))
								.add(Terminal.fromChar('_'))).min(1))
						.add(new Reference("interstitial"))
		);
		grammar.add("name", new Reference("identifier"));
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
		grammar.add("reference", new Reference("identifier"));
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
		grammar.add("terminal_escape", new Sequence().add(new Drop(Terminal.fromChar('\\'))).add(new Wildcard()));
		grammar.add("wildcard", new Sequence().add(Terminal.fromChar('.')).add(new Reference("interstitial")));
		grammar.add(
				"terminal",
				new Union()
						.add(new Sequence()
								.add(new Drop(Terminal.fromChar('[')))
								.add(new Repeat(new Union()
										.add(new Not(Terminal.fromChar('\\', ']')))
										.add(new Reference("terminal_escape"))).min(1))
								.add(new Drop(Terminal.fromChar(']')))
								.add(new Reference("interstitial")))
						.add(new Sequence()
								.add(new Drop(Terminal.fromChar('\'')))
								.add(new Union()
										.add(new Not(Terminal.fromChar('\\', '\'')))
										.add(new Reference("terminal_escape")))
								.add(new Drop(Terminal.fromChar('\'')))
								.add(new Reference("interstitial")))
		);
		grammar.add(
				"string",
				new Sequence()
						.add(new Drop(Terminal.fromChar('"')))
						.add(new Repeat(new Union()
								.add(new Not(Terminal.fromChar('\\', '"')))
								.add(new Sequence().add(new Drop(Terminal.fromChar('\\'))).add(new Wildcard()))).min(1))
						.add(new Drop(Terminal.fromChar('"')))
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
				new Sequence().add(Parse.stringSeq("//")).add(new Not(new Reference("eol"))).add(new Reference("eol"))
		);
		grammar.add(
				"interstitial1",
				new Drop(new Repeat(new Union()
						.add(Terminal.fromChar(' ', '\t'))
						.add(new Reference("eol"))
						.add(new Reference("comment"))).min(1))
		);
		grammar.add("interstitial", new Repeat(new Reference("interstitial1")).max(1));
		grammar.add(
				"eol",
				new Drop(new Union()
						.add(new Sequence().add(new Terminal((byte) 0x0D)).add(new Terminal((byte) 0x0A)))
						.add(new Terminal((byte) 0x0A)))
		);
	}

	public static Parse<Grammar> parse() {
		if (grammar == null)
			buildGrammar();
		return new Parse<Grammar>()
				.grammar(grammar)
				.node("root")
				.stack(() -> 0)
				.callbacks(new ImmutableMap.Builder<String, Callback<ClipStore>>().put("root", (store) -> {
					Grammar grammar = new Grammar();
					store = (ClipStore) Helper.<Pair<String, Node>>stackPopSingleList(store, (pair) -> {
						grammar.add(pair.first, pair.second);
					});
					return store.pushStack(grammar);
				}).put("rule", (store) -> {
					return Helper.stackDoubleElement(store);
				}).put("name", (store) -> {
					return store.pushStack(store.topData().toString());
				}).put("reference", (store) -> {
					return store.pushStack(new Reference(store.topData().toString()));
				}).put("union", (store) -> {
					Node right = store.stackTop();
					store = (ClipStore) store.popStack();
					Node left = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Union().add(left).add(right));
				}).put("sequence", (store) -> {
					Node right = store.stackTop();
					store = (ClipStore) store.popStack();
					Node left = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Sequence().add(left).add(right));
				}).put("terminal_escape", (store) -> {
					// TODO \xNN escapes
					byte top = store.topData().dataFirst();
					if (top == (byte) 'r')
						return store.setData(new Clip((byte) '\r'));
					if (top == (byte) 'n')
						return store.setData(new Clip((byte) '\n'));
					if (top == (byte) 't')
						return store.setData(new Clip((byte) '\t'));
					return store;
				}).put("wildcard", (store) -> {
					return store.pushStack(new Wildcard());
				}).put("terminal", (store) -> {
					return store.pushStack(new Terminal(store.topData().dataRender()));
				}).put("string", (store) -> {
					return store.pushStack(Parse.byteSeq(store.topData().dataRender()));
				}).put("drop", (store) -> {
					Node child = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Drop(child));
				}).put("not", (store) -> {
					Node child = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Not(child));
				}).put("repone", (store) -> {
					Node child = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Repeat(child).max(1));
				}).put("repmin", (store) -> {
					Node child = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Repeat(child).min(1));
				}).put("rep", (store) -> {
					Node child = store.stackTop();
					store = (ClipStore) store.popStack();
					return store.pushStack(new Repeat(child));
				}).build());
	}
}
