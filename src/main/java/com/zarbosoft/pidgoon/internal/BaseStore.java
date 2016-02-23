package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.source.Store;

public abstract class BaseStore implements Store {
	protected BranchingStack<Object> stack;
	protected BranchingStack<Object> data;
	
	protected abstract Object createEmptyData();
	protected abstract Object combineData(Object first, Object second);

	public BaseStore() { 
		stack = null;
		data = new BranchingStack<>(createEmptyData());
	}

	protected BaseStore(BaseStore other) { 
		stack = other.stack;
		data = other.data;
	}
	
	@Override
	public void popData(boolean combine) {
		Object out = data.top();
		data = data.pop();
		if (combine)
			addData(out);
	}

	@Override
	public void addData(Object top) {
		this.data = this.data.set(combineData(this.data.top(), top));
	}

	@Override
	public BaseStore pushData() {
		data = data.push(createEmptyData());
		return this;
	}

	@Override
	public void injectDataStack(long size) {
		Object temp = data.top();
		data = data.pop();
		for (long i = 0; i < size; ++i) pushData();
		data = data.push(temp);
	}

	public void setData(Object c) {
		this.data = this.data.set(c);
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
