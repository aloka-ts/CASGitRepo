/**
 * Created on Dec 22nd, 2005
 */

package com.baypackets.ase.util;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

public class AseUnmodifiableSet implements Set {
	private Set _origSet;

	public AseUnmodifiableSet(Set s) {
		_origSet = s;
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException("AseUnmodifiableSet.add(Object) not supported");
	}

	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException("AseUnmodifiableSet.addAll(Collection) not supported");
	}

	public void clear() {
		throw new UnsupportedOperationException("AseUnmodifiableSet.clear() not supported");
	}

	public boolean contains(Object o) {
		return _origSet.contains(o);
	}

	public boolean containsAll(Collection c) {
		return _origSet.containsAll(c);
	}

	public boolean equals(Object o) {
		return _origSet.equals(o);
	}

	public boolean isEmpty() {
		return _origSet.isEmpty();
	}

	public Iterator iterator() {
		return new AseUnmodifiableIterator(_origSet.iterator());
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException("AseUnmodifiableSet.remove(Object) not supported");
	}

	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException("AseUnmodifiableSet.removeAll(Collection) not supported");
	}

	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException("AseUnmodifiableSet.retainAll(Collection) not supported");
	}

	public int size() {
		return _origSet.size();
	}

	public Object[] toArray() {
		return _origSet.toArray();
	}

	public Object[] toArray(Object[] a) {
		return _origSet.toArray(a);
	}
}

