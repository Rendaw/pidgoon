package com.zarbosoft.undepurseable.bytes;

import com.zarbosoft.undepurseable.bytes.internal.Callback;
import com.zarbosoft.undepurseable.internal.BaseCapture;
import com.zarbosoft.undepurseable.internal.Node;
import com.zarbosoft.undepurseable.source.Store;

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
