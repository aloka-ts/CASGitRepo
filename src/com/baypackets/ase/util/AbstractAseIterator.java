/*
 * Created on Aug 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractAseIterator implements Iterator {

	protected Object criteria = null;
	protected Object next = null;
	protected int index = -1;
	protected List list = null;
	
	protected AbstractAseIterator(){}
		
	public AbstractAseIterator(Object criteria, List list){
		this.criteria = criteria;
		this.list = list;
		this.checkNext();
	}
		
	public synchronized boolean hasNext(){
		return (this.next != null);
	}
		
	public synchronized Object next(){
		if(this.next == null){
			throw new NoSuchElementException();
		}
		
		Object value = this.next;
		this.checkNext();
		return value;
	}
		
	public synchronized void remove(){
		if(next == null){
			throw new NoSuchElementException();
		}
			
		this.list.remove(this.index--);
		this.checkNext();
	}
		
	protected void checkNext(){
		this.next = null;
		while(++this.index < this.list.size()){
			Object temp = this.list.get(this.index);
			if(this.match(temp, criteria)){
				this.next = temp;			
				break;
			}
		}			
	}
	
	protected abstract boolean match(Object obj, Object criteria);
}
