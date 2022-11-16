/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.genband.threadpool;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Queue {
	
	private List list = Collections.synchronizedList(new LinkedList());
	
	private boolean stopped = false;
	
	private int producers = 0;
	private int consumers = 0;
	
	private int maxSize = Integer.MAX_VALUE;

	public Collection getElements() {
		return Collections.unmodifiableList(this.list);
	}

	public synchronized void enqueue(Object obj){
		
		producers++;
		while(!stopped){
			int size = list.size();
			if(size > this.maxSize){
				try{
					this.wait();
				}catch(InterruptedException e){}
				continue;
			}

			list.add(obj);
			if(size == 0 && consumers > 0){
				this.notifyAll();
			}
			break;
		}
		producers--;		
	}

	public synchronized Object dequeue(){
	
		consumers++;
		Object value = null;
		
		while(!this.stopped){
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
			break;
		}
		consumers--;
		return value;
	}
	
	public synchronized int dequeue(Object[] values){
	
		consumers++;
		int count = 0;
		if(values == null || values.length == 0)
			return count;
		
		while(!this.stopped){
			int size = list.size();
			if(size == 0){
				try{
					this.wait();
				}catch(InterruptedException e){}
				continue;
			}
		
			for(int i=0; i<size && i<values.length; i++){
				values[count++] = this.list.remove(0);
			}
			
			if(size > (this.maxSize - 100) 
				&& (size-count) < (this.maxSize - 100)){
				this.notifyAll();
			}
			break;
		}
		consumers--;
		return count;
	}
	
	public synchronized boolean isEmpty(){
		return this.list.isEmpty();
	}
	
	public synchronized int size(){
		return this.list.size();
	}
	
//	public synchronized void stop(){ // Part fix for BPInd13683 - "hang on 1 CPU m/c shutdown"
//		this.stopped = true;
//		this.notifyAll(); 
//	}
	public void stop(){
		this.stopped = true;
	}
}
