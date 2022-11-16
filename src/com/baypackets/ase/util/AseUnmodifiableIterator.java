/**
 * Created on Dec 22nd, 2005
 */

package com.baypackets.ase.util;

import java.util.Iterator;

public class AseUnmodifiableIterator implements Iterator {
	private Iterator _origIter;

	public AseUnmodifiableIterator(Iterator i) {
		_origIter = i;
	}

	public boolean hasNext() {
		return _origIter.hasNext();
	}

	public Object next() {
		return _origIter.next();
	}

	public void remove() {
		throw new UnsupportedOperationException("AseUnmodifiableIterator.remove() not supported");
	}
}

