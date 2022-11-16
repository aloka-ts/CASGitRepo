/**
 * Filename:	UnmodifiableIterator.java
 * Created on:	03-Oct-2006
 */

package com.baypackets.ase.ra.ro.util;

import java.util.Iterator;

public class UnmodifiableIterator implements Iterator {
	private Iterator _origIter;

	public UnmodifiableIterator(Iterator i) {
		_origIter = i;
	}

	public boolean hasNext() {
		return _origIter.hasNext();
	}

	public Object next() {
		return _origIter.next();
	}

	public void remove() {
		throw new UnsupportedOperationException("UnmodifiableIterator.remove() not supported");
	}
}

