package com.zarbosoft.pidgoon.internal;

public abstract class BaseStore extends Store {
	protected final BranchingStack<Object> stack;

	protected abstract Store split(BranchingStack<Object> stack);

	public BaseStore() {
		stack = null;
		color = null;
	}

	protected BaseStore(final BranchingStack<Object> stack, final Object color) {
		this.stack = stack;
		this.color = color;
	}

	@Override
	public <T> T stackTop() {
		return (T) stack.top();
	}

	@Override
	public Store popStack() {
		return split(stack.pop());
	}

	@Override
	public Store pushStack(final Object o) {
		if (stack == null) {
			return split(new BranchingStack<>(o));
		} else {
			return split(stack.push(o));
		}
	}

	@Override
	public boolean hasOneResult() {
		return (stack != null) && stack.isLast();
	}

}
