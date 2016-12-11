package com.zarbosoft.pidgoon.internal;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedBytes;
import com.zarbosoft.pidgoon.source.Store;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Helper {

	public static String byteFormat(final byte b) {
		if (b == (byte) '\n')
			return "\\n";
		if (b == (byte) '\r')
			return "\\r";
		if (b == (byte) '\t')
			return "\\t";
		if ((b < 32) || (b >= 127))
			return String.format("\\x%s", Strings.padStart(UnsignedBytes.toString(b), 2, '0'));
		return Character.toString((char) (byte) b);
	}

	public static String byteFormat(final List<Byte> bytes) {
		return bytes.stream().map(b -> byteFormat(b)).collect(Collectors.joining());
	}

	public static <T> Stream<T> stream(final T[] values) {
		return Arrays.asList(values).stream();
	}

	public static Store stackSingleElement(Store store) {
		final Object value = store.stackTop();
		store = store.popStack();
		final int length = store.stackTop();
		store = store.popStack();
		return store.pushStack(value).pushStack(length + 1);
	}

	public static Store stackDoubleElement(Store store) {
		final Object right = store.stackTop();
		store = store.popStack();
		final Object left = store.stackTop();
		store = store.popStack();
		final int length = store.stackTop();
		store = store.popStack();
		return store.pushStack(new Pair<>(left, right)).pushStack(length + 1);
	}

	public static Store stackDoubleElement(Store store, final String name) {
		final Object value = store.stackTop();
		store = store.popStack();
		final int length = store.stackTop();
		store = store.popStack();
		return store.pushStack(new Pair<>(name, value)).pushStack(length + 1);
	}

	public static <T> T uncheck(final Thrower1<T> code) {
		try {
			return code.get();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw new UncheckedException(e);
		}
	}

	public static void uncheck(final Thrower2 code) {
		try {
			code.get();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw new UncheckedException(e);
		}
	}

	public static <L, R> Store stackPopDoubleList(Store s, final Pair.Consumer<L, R> callback) {
		final Integer count = s.stackTop();
		s = s.popStack();
		return stackPopDoubleList(s, count, callback);
	}

	public static <L, R> Store stackPopDoubleList(Store s, final int length, final Pair.Consumer<L, R> callback) {
		for (int i = 0; i < length; ++i) {
			final Object l = s.stackTop();
			s = s.popStack();
			final Object r = s.stackTop();
			s = s.popStack();
			callback.accept((L) l, (R) r);
		}
		return s;
	}

	public static <T> Store stackPopSingleList(Store s, final Consumer<T> callback) {
		final Integer count = s.stackTop();
		s = s.popStack();
		return stackPopSingleList(s, count, callback);
	}

	public static <T> Store stackPopSingleList(Store s, final int length, final Consumer<T> callback) {
		for (int i = 0; i < length; ++i) {
			callback.accept(s.stackTop());
			s = s.popStack();
		}
		return s;
	}

	public static <T> T last(final List<T> values) {
		return values.get(values.size() - 1);
	}

	public static <T> Stream<T> stream(final Iterator<T> iterator) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
	}

	public static <T> Stream<Pair<Integer, T>> enumerate(final Stream<T> stream) {
		return enumerate(stream, 0);
	}

	public static <T> Stream<Pair<Integer, T>> enumerate(final Stream<T> stream, final int start) {
		final Mutable<Integer> count = new Mutable<>(start);
		return stream.map(e -> new Pair<>(count.value++, e));
	}

	@FunctionalInterface
	public interface Thrower1<T> {
		T get() throws Throwable;
	}

	@FunctionalInterface
	public interface Thrower2 {
		void get() throws Throwable;
	}

	public static class UncheckedException extends RuntimeException {
		private static final long serialVersionUID = 9029838186087025315L;

		public UncheckedException(final Throwable e) {
			super(e);
		}
	}
}
