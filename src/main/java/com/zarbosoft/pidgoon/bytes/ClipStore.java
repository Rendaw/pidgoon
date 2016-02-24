package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.internal.BaseStore;
import com.zarbosoft.pidgoon.internal.BranchingStack;
import com.zarbosoft.pidgoon.source.Store;

public class ClipStore extends BaseStore {

	protected BranchingStack<Clip> data;

	public ClipStore() {
		super();
		data = new BranchingStack<>(new Clip());
	}

	private ClipStore(ClipStore base) {
		super(base);
		data = base.data;
	}

	@Override
	public Store split() {
		return new ClipStore(this);
	}

	public Clip topData() {
		return data.top();
	}

	@Override
	public void pop(boolean combine) {
		Clip out = data.top();
		data = data.pop();
		if (combine)
			addData(out);
	}

	public void addData(Clip top) {
		this.data = this.data.set(this.data.top().cat(top));
	}

	@Override
	public BaseStore push() {
		data = data.push(new Clip());
		return this;
	}

	@Override
	public void inject(long size) {
		Clip temp = data.top();
		data = data.pop();
		for (long i = 0; i < size; ++i) push();
		data = data.push(temp);
	}

	public void setData(Clip c) {
		this.data = this.data.set(c);
	}

	@Override
	public void record(com.zarbosoft.pidgoon.source.Position position) {
		addData(((Position)position).getStoreData());
	}

	public Object peekStack() {
		return stack == null ? null : stack.top();
	}
	
}
