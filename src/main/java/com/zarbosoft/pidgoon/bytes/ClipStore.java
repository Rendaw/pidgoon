package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.internal.BaseStore;
import com.zarbosoft.pidgoon.internal.BranchingStack;
import com.zarbosoft.pidgoon.internal.Store;

public class ClipStore extends BaseStore {

	protected final BranchingStack<Clip> data;

	public ClipStore() {
		super();
		data = new BranchingStack<>(new Clip());
	}

	private ClipStore(final BranchingStack<Object> stack, final BranchingStack<Clip> data, final Object color) {
		super(stack, color);
		this.data = data;
	}

	@Override
	protected Store split(final BranchingStack<Object> stack) {
		return new ClipStore(stack, data, color);
	}

	public Clip topData() {
		return data.top();
	}

	@Override
	public Store pop(final boolean combine) {
		final Clip top = data.top();
		final BranchingStack<Clip> above = data.pop();
		if (combine)
			return new ClipStore(stack, above.set(above.top().cat(top)), color);
		else
			return new ClipStore(stack, above, color);
	}

	@Override
	public BaseStore push() {
		return new ClipStore(stack, data.push(new Clip()), color);
	}

	@Override
	public Store inject(final long size) {
		final Clip top = data.top();
		BranchingStack<Clip> pointer = data.pop();
		for (long i = 0; i < size; ++i)
			pointer = pointer.push(new Clip());
		pointer = pointer.push(top);
		return new ClipStore(stack, pointer, color);
	}

	public Store setData(final Clip c) {
		return new ClipStore(stack, this.data.set(c), color);
	}

	@Override
	public Store record(final com.zarbosoft.pidgoon.internal.Position position) {
		return new ClipStore(stack, data.set(data.top().cat(((Position) position).getStoreData())), color);
	}
}
