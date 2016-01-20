package com.zarbosoft.undepurseable.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.zarbosoft.undepurseable.InvalidGrammar;
import com.zarbosoft.undepurseable.internal.Aux;
import com.zarbosoft.undepurseable.internal.Clip;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.internal.Parent;
import com.zarbosoft.undepurseable.internal.Position;
import com.zarbosoft.undepurseable.internal.Store;
import com.zarbosoft.undepurseable.internal.TerminalContext;
import com.zarbosoft.undepurseable.nodes.Reference.RefParent;

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
	public void context(Position startPosition, Store store, Parent parent, Map<String, RefParent> seen) {
		Node outer = this;
		startPosition.addLeaf(new TerminalContext() {
			@Override
			public String toString() {
				return parent.buildPath(outer.toString());
			}
			
			@Override
			public void parse(Position position) {
				if (value.contains(position.get())) {
					if (cut) parent.cut(position);
					if (!drop) store.addData(new Clip(position));
					parent.advance(position, store);
				} else {
					parent.error(this);
				}
			}
		});
	}

}
