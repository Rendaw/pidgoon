package com.zarbosoft.pidgoon.events;

import com.zarbosoft.pidgoon.internal.BaseStore;
import com.zarbosoft.pidgoon.internal.BranchingStack;
import com.zarbosoft.pidgoon.source.Position;

public class Store extends BaseStore {

	private final Event top;

	public Store() {
		this.top = null;
	}

	public Store(final BranchingStack<Object> stack, final Event top) {
		super(stack);
		this.top = top;
	}

	@Override
	public com.zarbosoft.pidgoon.source.Store split(final BranchingStack<Object> stack) {
		return new Store(stack, top);
	}

	@Override
	public com.zarbosoft.pidgoon.source.Store pop(final boolean combine) {
		return this;
	}

	@Override
	public com.zarbosoft.pidgoon.source.Store push() {
		return this;
	}

	@Override
	public com.zarbosoft.pidgoon.source.Store inject(final long size) {
		return this;
	}

	@Override
	public com.zarbosoft.pidgoon.source.Store record(final Position position) {
		return new Store(stack, ((com.zarbosoft.pidgoon.events.Position) position).get());
	}

	public Event top() {
		return top;
	}

}
