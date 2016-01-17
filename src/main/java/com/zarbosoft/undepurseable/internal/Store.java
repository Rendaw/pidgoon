package com.zarbosoft.undepurseable.internal;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.primitives.Bytes;

public class Store {
	public Deque<Object> stack = new ArrayDeque<Object>();
	protected List<Range<Long>> dataAbsolute = new ArrayList<>();
	protected RangeMap<Long, byte[]> dataBuffers = TreeRangeMap.create();

	public Store() { }

	public void add(Position position) {
		if (dataBuffers.get(position.absolute) == null) {
			long start = position.absolute - position.localOffset;
			dataBuffers.put(Range.closedOpen(start, start + position.buf.length), position.buf);
		}
		Range<Long> absolute = Range.closedOpen(position.absolute, position.absolute + 1L);
		if (!dataAbsolute.isEmpty()) {
			int lastIndex = dataAbsolute.size() - 1;
			Range<Long> last = dataAbsolute.get(lastIndex);
			if (last.upperEndpoint() == absolute.lowerEndpoint()) {
				dataAbsolute.set(lastIndex, last.span(absolute));
				return;
			}
		}
		dataAbsolute.add(absolute);
	}

	public void add(Store store) {
		stack.addAll(store.stack);
		dataAbsolute.addAll(store.dataAbsolute);
		dataBuffers.putAll(store.dataBuffers);
	}

	public String dataString() {
		return new String(Bytes.toArray(dataRender()), StandardCharsets.UTF_8);
	}

	public byte dataFirst() {
		long first = dataAbsolute.get(0).lowerEndpoint();
		Entry<Range<Long>, byte[]> entry = this.dataBuffers.getEntry(first);
		first = first - entry.getKey().lowerEndpoint();
		return entry.getValue()[(int) first];
	}
	
	public List<Byte> dataRender() {
		List<Byte> out = new ArrayList<>();
		dataAbsolute.forEach(chunk -> {
			Set<Entry<Range<Long>, byte[]>> bufferSet = dataBuffers.subRangeMap(chunk).asMapOfRanges().entrySet();
			Entry<Range<Long>, byte[]> first = dataBuffers.getEntry(chunk.lowerEndpoint());
			out.addAll(Bytes.asList(first.getValue()).subList(
				(int)(long)(chunk.lowerEndpoint() - first.getKey().lowerEndpoint()), 
				(int)(chunk.intersection(first.getKey()).upperEndpoint() - first.getKey().lowerEndpoint())));
			bufferSet.stream().skip(1).forEach(buffer -> {
				long start = (int)(long)buffer.getKey().lowerEndpoint();
				out.addAll(Bytes.asList(buffer.getValue()).subList(
					(int)start, 
					(int)(buffer.getKey().upperEndpoint() - start)));
			});
		});
		return out;
	}

	public Store drop() {
		Store out = new Store();
		out.stack = stack;
		return out;
	}

	public Store split() {
		Store out = new Store();
		out.add(this);
		return out;
	}
}
