package com.zarbosoft.pidgoon.bytes;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.zarbosoft.pidgoon.InvalidGrammar;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.internal.Helper;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.State;
import com.zarbosoft.pidgoon.internal.Store;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Terminal extends Node {
	private final RangeSet<Byte> value;

	public Terminal(final RangeSet<Byte> value) {
		this.value = value;
	}

	public static Terminal fromChar(final char... units) {
		final Byte[] converted = new Byte[units.length];
		for (int i = 0; i < units.length; ++i)
			converted[i] = (Byte) (byte) units[i];
		return new Terminal(converted);
	}

	@SafeVarargs
	public Terminal(final Byte... units) {
		if (units.length == 0)
			throw new InvalidGrammar("Empty terminal specification!");
		final ImmutableRangeSet.Builder<Byte> builder = ImmutableRangeSet.builder();
		for (final Byte unit : units) {
			builder.add(Range.closedOpen(unit, (byte) (unit + 1)));
		}
		value = builder.build();
	}

	public Terminal(Range<Byte> range) {
		range = Range.closedOpen(
				(byte) (range.lowerBoundType() == BoundType.CLOSED ? range.lowerEndpoint() : range.lowerEndpoint() - 1),
				(byte) (range.upperBoundType() == BoundType.OPEN ? range.upperEndpoint() : range.upperEndpoint() + 1)
		);
		final ImmutableRangeSet.Builder<Byte> builder = ImmutableRangeSet.builder();
		builder.add(range);
		value = builder.build();
	}

	public Terminal(final List<Byte> units) {
		final ImmutableRangeSet.Builder<Byte> builder = ImmutableRangeSet.builder();
		for (final Byte unit : units) {
			builder.add(Range.closedOpen(unit, (byte) (unit + 1)));
		}
		value = builder.build();
	}

	public String toString() {
		final List<String> single = new ArrayList<>();
		final List<String> range = new ArrayList<>();
		value.asRanges().stream().forEach(r -> {
			if (r.upperEndpoint() == r.lowerEndpoint() + 1) {
				single.add(Helper.byteFormat(r.lowerEndpoint()));
			} else {
				range.add(String.format("%s-%s",
						Helper.byteFormat(r.lowerEndpoint()),
						Helper.byteFormat((byte) (r.upperEndpoint() - 1))
				));
			}
		});
		if (range.isEmpty() && (single.size() == 1))
			return String.format("'%s'", single.get(0));
		return String.format("[%s%s]",
				single.stream().collect(Collectors.joining()),
				range.stream().collect(Collectors.joining())
		);
	}

	@Override
	public void context(
			final ParseContext context,
			final Store prestore,
			final Parent parent,
			final PMap<Object, RefParent> seen,
			final Object cause
	) {
		final Node outer = this;
		context.leaves.add(new State() {
			@Override
			public String toString() {
				return parent.buildPath(outer.toString());
			}

			@Override
			public <T> T color() {
				return (T) prestore.color;
			}

			@Override
			public void parse(final ParseContext step, final com.zarbosoft.pidgoon.internal.Position sourcePosition) {
				Store store = prestore;
				final Position position = (Position) sourcePosition;
				store = store.record(position);
				if (value.contains(position.get())) {
					parent.advance(step, store, this);
				} else {
					parent.error(step, store, this);
				}
			}
		});
	}

}
