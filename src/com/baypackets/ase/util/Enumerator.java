/*
 * Created on Aug 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Enumerator implements Enumeration {
	
	private Iterator iterator = null;

	public Enumerator(Iterator iterator){
		this.iterator = iterator;
	}

	public boolean hasMoreElements() {
		return (iterator == null) ? false : iterator.hasNext();
	}

	public Object nextElement() {
		if(iterator != null){
			return iterator.next();
		}else{
			throw new NoSuchElementException();
		}
	}
}
