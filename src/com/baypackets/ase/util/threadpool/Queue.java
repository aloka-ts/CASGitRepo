/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util.threadpool;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.baypackets.ase.util.AseUtils;

import org.apache.log4j.Logger;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Queue {
	
	private int index = 0;
	private BlockingQueue list = null; 
	private BlockingQueue priorityList = null; 
	private boolean stopped = false;
	
	//private int producers = 0;
	//private int consumers = 0;
	
	private int maxSize = Integer.MAX_VALUE;

	private int policy = POLICY_UNBOUNDED;

	private static Logger m_l = Logger.getLogger(Queue.class.getName());

	// Size will be unlimited
	public static final int POLICY_UNBOUNDED = 0;

	// Replace the last message in the Queue when Q gets full
	public static final int POLICY_DROP_FIRST = 1;
	
	//Drop the new comming message whan Q gets full;
	public static final int POLICY_DROP_LAST = 2;



	public Queue()	{
		//list = Collections.synchronizedList(new LinkedList());
		list = new LinkedBlockingQueue();
		if(AseUtils.getCallPrioritySupport() == 1) {
			if(m_l.isDebugEnabled())
				m_l.debug(" Call priority is supported so create priority List");
			//priorityList = Collections.synchronizedList(new LinkedList());
			priorityList = new LinkedBlockingQueue();
		}
	}
		

	public Collection getElements() {
		return Collections.unmodifiableList(Arrays.asList(this.list.toArray()));
	}

	public void enqueue(Object obj){
		
		//producers++;
		while(!stopped) {
			//int size = list.size();
			if(this.policy == POLICY_UNBOUNDED) {
				list.offer(obj);
			} else if(list.size() >= this.maxSize) {
				if(this.policy == POLICY_DROP_FIRST) {
					synchronized (this) {
						if (list.size() >= this.maxSize) {
							list.poll();
						}
						list.offer(obj);
					}
					
				} else {
					m_l.error("Queue is full: Droping the message");
				}
			} else {
				list.offer(obj);
			}
			/*
			if(size == 0 && consumers > 0){
				this.notifyAll();
			}*/
			break;
		}
		//producers--;		
	}

	public void enqueue(Object obj,boolean priorityMsg) {
		//producers++;
		int size;
		while(!stopped) {
			if(priorityList != null && priorityMsg)	{//add message to priority list
				size = priorityList.size();
				if(this.policy == POLICY_UNBOUNDED) {
					if(m_l.isDebugEnabled())
						m_l.debug("Adding message to priority List.");
					priorityList.offer(obj);
				} else if(size > this.maxSize) {
					if(this.policy == POLICY_DROP_FIRST) {
						synchronized (this) {
							if (priorityList.size() > this.maxSize) {
								priorityList.poll();
							}
							priorityList.offer(obj);
						}
					} else { 
						m_l.error("Queue size exceeded: Dropping the message");
					}
				} else {
					priorityList.offer(obj);
				}
				
				/*
				if (size == 0) {
					// Notify in the priority queue
					synchronized (this) {
						this.notify();
					}
				}*/
				
				/*
				 if(size == 0 && consumers > 0){
					this.notifyAll();
				}
				break;
				*/
			} else	{//add message to normal list
				//size = list.size();
				if(this.policy == POLICY_UNBOUNDED) {
					if(m_l.isDebugEnabled())
						m_l.debug("Adding message to normal List ");
					list.offer(obj);
				} else if(list.size() > this.maxSize) {
					if(this.policy == POLICY_DROP_FIRST) {
						synchronized (this) {
							if (list.size() > this.maxSize) {
								list.poll();
							}
							list.offer(obj);
						}
					} else { 
						m_l.error("Queue size exceeded: Dropping the message");
					}
				} else {
					list.offer(obj);
				}

				/*if(size == 0 && consumers > 0){
					this.notifyAll();
				}
				break;
				*/
			}
			break;
		}
		//producers--;		
	}
	
	public Object dequeue() {
	
		//consumers++;
		//Object value = null;
		int size = 0;
		while(!this.stopped) {
			/*
			int size = list.size();
			if(size == 0){
				try{
					this.wait();
				}catch(InterruptedException e){}
				continue;
			}
			
			value = this.list.remove(0);
			if(size == (this.maxSize - 100)){
				this.notifyAll();
			}
			break;*/
			
			try {
				if((priorityList != null) && (AseUtils.getCallPrioritySupport() == 1))	{
					//Check if priority list is empty
					size = priorityList.size();
					if (size == 0) {
						// priority list is empty so
						// dequeue from normal list
						if (m_l.isDebugEnabled()) {
							m_l.debug("Priority list is empty");
						}
						return this.list.take();
					} else {
						return this.priorityList.take();
					}
				} else {
					return this.list.take();
				}
			} catch (InterruptedException ex) {
				m_l.info("Thread interrupted, check stopped flag");
			}
		}
		//consumers--;
		return null;
	}

	public int dequeue(Object[] values){
	
		//consumers++;
		int count = 0;
		int size;
		if(values == null || values.length == 0)
			return count;
		
		while(!this.stopped) {
			if((priorityList != null) && (AseUtils.getCallPrioritySupport() == 1))	{
				//Check if priority list is empty
				size = priorityList.size();
				if (size == 0) {
					// priority list is empty so
					// dequeue from normal list
					if (m_l.isDebugEnabled()) {
						m_l.debug("Priority list is empty");
					}

					size = list.size();
					if (size == 0) {
						try {
							synchronized (this) {
								this.wait();
							}
						} catch (InterruptedException e) {
							m_l.info("Thread interrupted, check stopped flag");
						}
						continue;
					}
					// Set priority of current thread to normal
					if (m_l.isDebugEnabled())
						m_l.debug(" processing message from normal List");
					if (Thread.currentThread().getPriority() != Thread.NORM_PRIORITY)
						Thread.currentThread()
								.setPriority(Thread.NORM_PRIORITY);

					for (int i = 0; i < size && i < values.length; i++) {
						if (m_l.isDebugEnabled()) {
							m_l.debug("getting object from normal list");
						}
						values[count++] = this.list.poll();
					}

				} else {
					// priority list is not empty so
					// dequeue from priority list
					if (m_l.isDebugEnabled())
						m_l.debug("processing message from priority List");
					for (int i = 0; i < size && i < values.length; i++) {
						values[count++] = this.priorityList.poll();
					}
				}
			} else	{
				size = list.size();
				if (size == 0) {
					try {
						synchronized (this) {
							this.wait();
						}
					} catch (InterruptedException e) {
						m_l.info("Thread interrupted, check stopped flag");
					}
					continue;
				}
		
				if(m_l.isDebugEnabled())
					m_l.debug("TESTPK: processing message from normal List");
				for(int i=0; i<size && i<values.length; i++) {
					values[count++] = this.list.poll();
				}
			
			}
			/*
			if (size > (this.maxSize - 100)
					&& (size - count) < (this.maxSize - 100)) {
				this.notifyAll();
			}
			*/
			break;
		}
		//consumers--;
		return count;
	}
	
	public boolean isEmpty(){
		if(priorityList != null)	{
			return (this.list.isEmpty() && this.priorityList.isEmpty());
		}else	{
			return this.list.isEmpty();
		}
	}
	
	public int size(){
		if(priorityList != null)        {
			return (this.list.size() + this.priorityList.size());
		}else	{
			return this.list.size();
		}
	}
	
//	public synchronized void stop(){ // Part fix for BPInd13683 - "hang on 1 CPU m/c shutdown"
//		this.stopped = true;
//		this.notifyAll(); 
//	}
	public void stop(){
		this.stopped = true;
	}

	public void setMaxQueueSize(int size) {
		if(m_l.isDebugEnabled()) {
			m_l.debug("Setting the max Queue Size = "+size);
		}
		this.maxSize = size;
	}

	public void setPolicy(int policy) {
		if(m_l.isDebugEnabled()) {
			m_l.debug("Setting the Queue Policy as "+policy); 
		}
		this.policy = policy;
	}

	public int getPolicy() {
		return this.policy;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
