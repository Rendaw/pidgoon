package com.zarbosoft.pidgoon.bytes;

import com.google.common.primitives.Bytes;
import com.zarbosoft.pidgoon.InvalidStream;
import com.zarbosoft.pidgoon.internal.BaseParse;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Parse<O> extends BaseParse<Parse<O>> {
	private Parse(final Parse<O> other) {
		super(other);
	}

	public Parse() {
	}

	public O parse(final String string) {
		return parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}

	public O parse(final InputStream stream) {
		ClipStore store = new ClipStore();
		if (initialStack != null)
			store = (ClipStore) store.pushStack(initialStack.get());
		Position position = new Position(stream);
		ParseContext context =
				grammar.prepare(node, callbacks, store, errorHistoryLimit, uncertaintyLimit, dumpAmbiguity);
		if (position.isEOF())
			return null;
		while (!position.isEOF()) {
			/*
			if (context.ambiguityHistory != null)
				System.out.println(String.format(
						"\n%d ==============\n%d\n%s\n%s\n",
						context.ambiguityHistory.top().step,
						context.hashCode(),
						position,
						context.leaves.stream().map(l -> l.toString()).collect(Collectors.joining("\n"))
				));
			*/
			context = grammar.step(context, position);
			position = position.advance();
		}
		if (context.results.isEmpty())
			return null;
		return (O) context.results.get(0);
	}

	public static class Match<O> {
		public Position end;
		public List<O> results;
	}

	public Match<O> longestMatchFromStart(final InputStream stream) {
		ClipStore store = new ClipStore();
		if (initialStack != null)
			store = (ClipStore) store.pushStack(initialStack.get());
		Position position = new Position(stream);
		ParseContext context =
				grammar.prepare(node, callbacks, store, errorHistoryLimit, uncertaintyLimit, dumpAmbiguity);
		if (position.isEOF())
			return null;
		Match<O> match = null;
		while (!position.isEOF()) {
			try {
				context = grammar.step(context, position);
			} catch (final InvalidStream e) {
				break;
			}
			position = position.advance();
			match = new Match<>();
			match.end = position;
			match.results = (List<O>) context.results;
		}
		return match;
	}

	public static Sequence byteSeq(final List<Byte> list) {
		final Sequence out = new Sequence();
		list.stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	public static Sequence stringSeq(final String string) {
		final Sequence out = new Sequence();
		Bytes.asList(string.getBytes(StandardCharsets.UTF_8)).stream().forEach(b -> out.add(new Terminal(b)));
		return out;
	}

	@Override
	protected Parse<O> split() {
		return new Parse<>(this);
	}
}
