package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.source.Store;

public abstract class BaseStore implements Store {
	protected BranchingStack<Object> stack;

	public BaseStore() { 
		stack = null;
	}

	protected BaseStore(BaseStore other) { 
		stack = other.stack;
	}
	
	@Override
	public Object popStack() {
		Object out = stack.top();
		stack = stack.pop();
		return out;
	}
	
	@Override
	public Store pushStack(Object o) {
		if (stack == null) {
			stack = new BranchingStack<Object>(o);
		} else {
			stack = stack.push(o);
		}
		return this;
	}

	@Override
	public boolean hasOneResult() {
		return (stack != null) && stack.isLast();
	}

	@Override
	public Object takeResult() {
		Object out = stack.top();
		stack = stack.pop();
		return out;
	}


}
