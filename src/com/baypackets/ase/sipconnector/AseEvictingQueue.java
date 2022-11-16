/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/
package com.baypackets.ase.sipconnector;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class represents a Queue with a fixed maximum size - i.e. it always allows addition of elements, 
 * but it will silently remove head elements to accommodate space for newly added elements.  
 * @author Amit Baxi
 *
 */

public class AseEvictingQueue<E> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8669784269481508L;
	
	private LinkedList<E> m_list;
	private short m_maxCapacity;
	
	/**
	 * Constructs an empty list with the specified maximum capacity. 
	 * @param maxCapacity the maximum capacity of the list.
	 */
	public AseEvictingQueue(short maxCapacity) {
		if(maxCapacity<1){
			throw new IllegalArgumentException("Maximum capacity should be greater than zero.");
		}
		m_maxCapacity=maxCapacity;
		m_list=new LinkedList<E>();
		
	}
	
	/**
	 * This method will add element in queue and will remove element from head if queue size reaches to max capacity. 
	 * @param element
	 * @return
	 */
	public boolean add(E element){
		
		synchronized (m_list) {
			boolean isAdded = m_list.add(element);
			while (m_list.size() > m_maxCapacity) {
				m_list.remove();
			}
			return isAdded;
		}
	      
	}

	
	@Override
	public String toString() {
		synchronized (m_list) {
			return m_list.toString();
		}
	}
	
	
}
