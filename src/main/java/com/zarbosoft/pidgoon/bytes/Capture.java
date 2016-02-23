package com.zarbosoft.pidgoon.bytes;

import com.zarbosoft.pidgoon.bytes.internal.Callback;
import com.zarbosoft.pidgoon.internal.BaseCapture;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.source.Store;

public class Capture extends BaseCapture {

	public Capture(Node root, Callback callback) {
		super(root);
		this.callback = callback;
	}

	private Callback callback;

	@Override
	protected void callback(Store store) {
		callback.accept(store);
	}

}
