package com.zarbosoft.pidgoon.internal;

public class Mutable<T> {
	public T value;

	public Mutable(T value) {
		this.value = value;
	}

	public Mutable() {}
}