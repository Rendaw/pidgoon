package com.zarbosoft.undepurseable.internal;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;
import com.google.common.primitives.Bytes;

public class Store {
	final public Deque<Object> stack = new ArrayDeque<Object>();
	protected RangeSet<Long> dataAbsolute = TreeRangeSet.create();
	protected RangeMap<Long, byte[]> dataBuffers = TreeRangeMap.create();

	public Store() { }

	public Store(Store originalStore) {
		this();
		this.stack.addAll(originalStore.stack);
		this.dataAbsolute.addAll(originalStore.dataAbsolute);
		this.dataBuffers.putAll(originalStore.dataBuffers);
	}

	public Store split() {
		return new Store(this);
	}

	public void add(Position position) {
		if (dataBuffers.get(position.absolute) == null) {
			long start = position.absolute - position.localOffset;
			dataBuffers.put(Range.closedOpen(start, start + position.buf.length), position.buf);
		}
		dataAbsolute.add(Range.closedOpen(position.absolute, position.absolute + 1L));
	}

	public void dropData(Store originalStore) {
		dataAbsolute = TreeRangeSet.create();
		dataAbsolute.addAll(originalStore.dataAbsolute);
		dataBuffers = TreeRangeMap.create();
		dataBuffers.putAll(originalStore.dataBuffers);
	}

	public String dataString() {
		return new String(Bytes.toArray(dataRender()), StandardCharsets.UTF_8);
	}

	public byte dataFirst() {
		long first = dataAbsolute.span().lowerEndpoint();
		Entry<Range<Long>, byte[]> entry = this.dataBuffers.getEntry(first);
		first = first - entry.getKey().lowerEndpoint();
		return entry.getValue()[(int) first];
	}

	public List<Byte> dataRender() {
		List<Byte> out = new ArrayList<>();
		for (Entry<Range<Long>, byte[]> bufferEntry : dataBuffers.asMapOfRanges().entrySet()) {
			long offset = bufferEntry.getKey().lowerEndpoint();
			for (Range<Long> indexEntry : dataAbsolute.subRangeSet(bufferEntry.getKey()).asRanges()) {
				out.addAll(Bytes.asList(bufferEntry.getValue()).subList(
					(int)(indexEntry.lowerEndpoint() - offset), 
					(int)(indexEntry.upperEndpoint() - offset)));
			}
		}
		return out;
	}

}
