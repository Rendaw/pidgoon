package com.zarbosoft.pidgoon.internal;

import com.zarbosoft.pidgoon.AbortParse;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.ParseContext;
import com.zarbosoft.pidgoon.nodes.Reference.RefParent;
import org.pcollections.PMap;

import java.util.Map;

public abstract class BaseOperator extends Node {
	private final Node root;

	public BaseOperator() {
		super();
		root = null;
	}

	public BaseOperator(final Node root) {
		super();
		this.root = root;
	}

	protected abstract Store callback(Store store, Map<String, Object> callbacks);

	@Override
	public void context(
			final ParseContext context,
			final Store store,
			final Parent parent,
			final PMap<String, RefParent> seen,
			final Object cause
	) {
		if (root == null) {
			parent.advance(context, callback(store, context.callbacks).pop(true), cause);
		} else {
			root.context(context, store.push(), new BaseParent(parent) {

				@Override
				public void advance(final ParseContext step, final Store store, final Object cause) {
					Store tempStore = store;
					try {
						tempStore = callback(store, context.callbacks);
					} catch (final AbortParse a) {
						parent.error(step, tempStore, a);
						return;
					}
					parent.advance(step, tempStore.pop(true), cause);
				}

				@Override
				public String buildPath(final String subpath) {
					return parent.buildPath(String.format("op . %s", subpath));
				}
			}, seen, cause);
		}
	}

	public String toString() {
		return root.toString();
	}
}
