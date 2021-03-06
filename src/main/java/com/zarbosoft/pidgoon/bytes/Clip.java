package com.zarbosoft.pidgoon.bytes;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.primitives.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Clip {
	static long nonstreamCount = 0;
	protected List<Range<Long>> dataAbsolute = new ArrayList<>();
	protected RangeMap<Long, byte[]> dataBuffers = TreeRangeMap.create();

	public Clip() {

	}

	public Clip(final byte b) {
		dataBuffers.put(Range.closedOpen(nonstreamCount - 1, nonstreamCount), new byte[] {b});
		dataAbsolute.add(Range.closedOpen(nonstreamCount - 1, nonstreamCount));
		nonstreamCount -= 1;
	}

	public Clip(final Position position) {
		final long start = position.absolute - position.localOffset;
		dataBuffers.put(Range.closedOpen(start, start + position.buf.length), position.buf);
		dataAbsolute.add(Range.closedOpen(position.absolute, position.absolute + 1));
	}

	private void add(final Clip clip) {
		dataBuffers.putAll(clip.dataBuffers);
		if (!dataAbsolute.isEmpty() && !clip.dataAbsolute.isEmpty()) {
			final Range<Long> absolute = clip.dataAbsolute.get(0);
			final int lastIndex = dataAbsolute.size() - 1;
			final Range<Long> last = dataAbsolute.get(lastIndex);
			if (last.upperEndpoint().equals(absolute.lowerEndpoint())) {
				dataAbsolute.set(lastIndex, last.span(absolute));
				dataAbsolute.addAll(clip.dataAbsolute.subList(1, clip.dataAbsolute.size()));
				return;
			}
		}
		dataAbsolute.addAll(clip.dataAbsolute);

	}

	public Clip cat(final Clip clip) {
		final Clip out = new Clip();
		out.add(this);
		out.add(clip);
		return out;
	}

	public String toString() {
		return new String(Bytes.toArray(dataRender()), StandardCharsets.UTF_8);
	}

	public byte dataFirst() {
		long first = dataAbsolute.get(0).lowerEndpoint();
		final Entry<Range<Long>, byte[]> entry = this.dataBuffers.getEntry(first);
		first = first - entry.getKey().lowerEndpoint();
		return entry.getValue()[(int) first];
	}

	public List<Byte> dataRender() {
		final List<Byte> out = new ArrayList<>();
		dataAbsolute.forEach(chunk -> {
			final Set<Entry<Range<Long>, byte[]>> bufferSet = dataBuffers.subRangeMap(chunk).asMapOfRanges().entrySet();
			final Entry<Range<Long>, byte[]> first = dataBuffers.getEntry(chunk.lowerEndpoint());
			out.addAll(Bytes.asList(first.getValue()).subList(
					(int) (long) (chunk.lowerEndpoint() - first.getKey().lowerEndpoint()),
					(int) (chunk.intersection(first.getKey()).upperEndpoint() - first.getKey().lowerEndpoint())
			));
			bufferSet.stream().skip(1).forEach(buffer -> {
				final long start = (int) (long) buffer.getKey().lowerEndpoint();
				out.addAll(Bytes
						.asList(buffer.getValue())
						.subList((int) start, (int) (buffer.getKey().upperEndpoint() - start)));
			});
		});
		return out;
	}

}
