package com.zarbosoft.undepurseable.internal;

public class BranchingStack<T> {
	private T top;
	private BranchingStack<T> parent;
	
	public BranchingStack(T t) {
		top = t;
		parent = null;
	}

	private BranchingStack(BranchingStack<T> parent, T t) {
		top = t;
		this.parent = parent;
	}
	
	public T top() {
		return top;
	}
	
	public BranchingStack<T> push(T t) {
		return new BranchingStack<>(this, t);
	}
	
	public BranchingStack<T> pop() {
		return parent;
	}

	public BranchingStack<T> set(T t) {
		return new BranchingStack<>(parent, t);
	}
	
	private long size(long start) {
		if (parent == null) return start + 1;
		return parent.size(start + 1);
	}

	public long size() {
		return size(0);
	}
	
	private String toString(String suffix) {
		String text = String.format("%s, %s", top, suffix);
		if (parent == null) return text;
		return parent.toString(text);
	}
	
	public String toString() {
		String text = top.toString();
		if (parent != null) text = parent.toString(text);
		return String.format("[%s]", text);
	}
}
