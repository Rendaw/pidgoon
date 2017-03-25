package com.zarbosoft.pidgoon.internal;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedBytes;
import com.zarbosoft.rendaw.common.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
}
