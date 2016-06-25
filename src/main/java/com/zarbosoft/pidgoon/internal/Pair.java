package com.zarbosoft.pidgoon.internal;

import java.util.Collection;
import java.util.stream.Stream;

public class Pair<T1, T2> {
	@FunctionalInterface
	public interface Consumer<T1, T2> {
		void accept(T1 a, T2 b);
	}

	@FunctionalInterface
	public interface Function<R, T1, T2> {
		R accept(T1 a, T2 b);
	}

	public T1 first;
	public T2 second;
	public Pair(T1 first, T2 second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public static <T> Stream<Pair<Integer, T>> enumerate(Collection<T> collection) {
		return collection.stream().map(new java.util.function.Function<T, Pair<Integer, T>>() {
			int index = 0;

			@Override
			public Pair<Integer, T> apply(T t) {
				return new Pair<>(index++, t);
			}
		});
	}
}
