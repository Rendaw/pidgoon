package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.BaseStore;
import com.zarbosoft.pidgoon.internal.BranchingStack;
import com.zarbosoft.pidgoon.internal.Position;

public class Store extends BaseStore {

	private final Event top;

	public Store() {
		this.top = null;
	}

	public Store(final BranchingStack<Object> stack, final Event top, final Object color) {
		super(stack, color);
		this.top = top;
	}

	@Override
	public com.zarbosoft.pidgoon.internal.Store split(final BranchingStack<Object> stack) {
		return new Store(stack, top, color);
	}

	@Override
	public com.zarbosoft.pidgoon.internal.Store pop(final boolean combine) {
		return this;
	}

	@Override
	public com.zarbosoft.pidgoon.internal.Store push() {
		return this;
	}

	@Override
	public com.zarbosoft.pidgoon.internal.Store inject(final long size) {
		return this;
	}

	@Override
	public com.zarbosoft.pidgoon.internal.Store record(final Position position) {
		return new Store(stack, ((com.zarbosoft.pidgoon.events.Position) position).get(), color);
	}

	public Event top() {
		return top;
	}

}
