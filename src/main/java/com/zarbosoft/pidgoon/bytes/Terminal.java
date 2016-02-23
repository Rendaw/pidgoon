package com.zarbosoft.pidgoon.bytes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.zarbosoft.pidgoon.InvalidGrammar;
import com.zarbosoft.pidgoon.internal.Aux;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Parent;
import com.zarbosoft.pidgoon.internal.ParseContext;
import com.zarbosoft.pidgoon.internal.TerminalReader;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import com.zarbosoft.pidgoon.source.Store;

public class Terminal extends Node {
	private RangeSet<Byte> value;
	
	public Terminal(RangeSet<Byte> value) {
		this.value = value;
	}

	public static Terminal fromChar(char ...units) {
		Byte[] converted = new Byte[units.length];
		for (int i = 0; i < units.length; ++i)
			converted[i] = (Byte)(byte)units[i];
		return new Terminal(converted);
	}
	
	@SafeVarargs
	public Terminal(Byte... units) {
		if (units.length == 0) throw new InvalidGrammar("Empty terminal specification!");
		ImmutableRangeSet.Builder<Byte> builder = ImmutableRangeSet.builder();
		for (Byte unit : units) {
			builder.add(Range.closedOpen(unit, (byte)(unit + 1)));
		}
		value = builder.build();
	}

	public Terminal(Range<Byte> range) {
		range = Range.closedOpen(
			(byte)(range.lowerBoundType() == BoundType.CLOSED ? range.lowerEndpoint() : range.lowerEndpoint() - 1), 
			(byte)(range.upperBoundType() == BoundType.OPEN ? range.upperEndpoint() : range.upperEndpoint() + 1));
		ImmutableRangeSet.Builder<Byte> builder = ImmutableRangeSet.builder();
		builder.add(range);
		value = builder.build();
	}

	public Terminal(List<Byte> units) {
		ImmutableRangeSet.Builder<Byte> builder = ImmutableRangeSet.builder();
		for (Byte unit : units) {
			builder.add(Range.closedOpen(unit, (byte)(unit + 1)));
		}
		value = builder.build();
	}
	
	public String toString() {
		List<String> single = new ArrayList<>();
		List<String> range = new ArrayList<>();
		value.asRanges().stream().forEach(r -> {
			if (r.upperEndpoint() == r.lowerEndpoint() + 1) {
				single.add(Aux.byteFormat(r.lowerEndpoint()));
			} else {
				range.add(String.format(
					"%s-%s", 
					Aux.byteFormat(r.lowerEndpoint()), 
					Aux.byteFormat((byte)(r.upperEndpoint() - 1))));
			}
		});
		if (range.isEmpty() && (single.size() == 1)) return String.format("'%s'", single.get(0));
		return String.format(
			"[%s%s]", 
			single.stream().collect(Collectors.joining()), 
			range.stream().collect(Collectors.joining()));
	}

	@Override
	public void context(ParseContext context, Store prestore, Parent parent, Map<String, RefParent> seen) {
		Node outer = this;
		context.outLeaves.add(new TerminalReader() {
			@Override
			public String toString() {
				return parent.buildPath(outer.toString());
			}
			
			@Override
			public void parse() {
				ClipStore store = (ClipStore)prestore;
				Position position = (Position) context.position;
				if (value.contains(position.get())) {
					if (cut) parent.cut();
					if (!drop) store.addData(position.getStoreData());
					parent.advance(store);
				} else {
					parent.error(this);
				}
			}
		});
	}

}
