package com.zarbosoft.undepurseable.internal;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public class Pair<T1, T2> {
	public T1 first;
	public T2 second;
	public Pair(T1 first, T2 second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public static <T> Stream<Pair<Integer, T>> enumerate(Collection<T> collection) {
		return collection.stream().map(new Function<T, Pair<Integer, T>>() {
			int index = 0;

			@Override
			public Pair<Integer, T> apply(T t) {
				return new Pair<>(index++, t);
			}
		});
	}
}
