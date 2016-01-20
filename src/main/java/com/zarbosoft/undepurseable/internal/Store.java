package com.zarbosoft.undepurseable.internal;

public class Store {
	public BranchingStack<Object> stack;
	public BranchingStack<Clip> data;

	public Store() { 
		stack = null;
		data = new BranchingStack<Clip>(new Clip());
	}

	private Store(Store other) { 
		stack = other.stack;
		data = other.data;
	}
	
	public Store split() {
		return new Store(this);
	}

	public Clip popData() {
		Clip out = data.top();
		data = data.pop();
		return out;
	}

	public Clip topData() {
		return data.top();
	}

	public void addData(Clip top) {
		this.data = this.data.set(this.data.top().cat(top));
	}

	public Store pushData() {
		data = data.push(new Clip());
		return this;
	}

	public void injectDataStack(long size) {
		Clip temp = data.top();
		data = data.pop();
		for (long i = 0; i < size; ++i) pushData();
		data = data.push(temp);
	}

	public void setData(Clip c) {
		this.data = this.data.set(c);
	}
	
	public Object popStack() {
		Object out = stack.top();
		stack = stack.pop();
		return out;
	}
	
	public Store pushStack(Object o) {
		if (stack == null) {
			stack = new BranchingStack<Object>(o);
		} else {
			stack = stack.push(o);
		}
		return this;
	}
}
