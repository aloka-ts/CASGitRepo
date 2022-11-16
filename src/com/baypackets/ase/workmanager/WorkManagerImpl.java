//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   WorkManagerImpl.java
//
//      Desc:   This file implements commonj.work.WorkManager interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           04/10/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.workmanager;

import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;
import commonj.work.WorkEvent;
import commonj.work.WorkException;

import com.baypackets.ase.util.threadpool.*;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.Integer;
import java.lang.String;
import java.util.*;
import java.lang.System;
import javax.naming.*;

/**
 * The WorkManager is the abstraction for dispatching and monitoring asynchronous work and 
 * is a factory for creating application short or long lived Works.
 *
 * This class also implements Referenceable to provide a reference to itself
 * @author Somesh Kr. Srivastava
 */
public class WorkManagerImpl implements WorkManager, Referenceable {

	private static Logger _logger = Logger.getLogger(WorkManagerImpl.class);

	private HashSet wrapSet;
	private ConfigRepository config;
	public int threadPoolSize = 8;
	public int queueSize = 100;
	public int maxDaemonThread = 4;
	private int daemonThreadCount = 0;
	private boolean isPoolStarted = false; //thread pool is being started only once when first time schedule is called
	private ThreadPool thp;
	String _workManager;
	String m_appId;
   
   /**
    * 
	*/
	public WorkManagerImpl() {
	if(_logger.isDebugEnabled())
		_logger.debug("inside WorkManager default constructor");
	}

	public WorkManagerImpl(String p_workManager, String p_appId) {
		if(_logger.isDebugEnabled())
		_logger.debug("In WorkManagerImpl(String, String)");
		_workManager = p_workManager;
		m_appId = p_appId;
	}

   
   /**
    * Dispatches a Work asynchronously. The work is dispatched and the method returns immediately. The J2EE context
  	* of the caller is used to execute the Work
  	* @param Work -the work to execute.
  	* @returns WorkItem representing the asynchronous work.
  	* @throws WorkException is queuing up results in an exception
  	* @throws IllegalArgumentException -if work is a javax.ejb.EnterpriseBeam.
  	*/
	public WorkItem schedule(Work work) throws WorkException, IllegalArgumentException {
		return schedule(work, null);	
	}

	
   /**Dispatches a Work asynchronously. The work is dispatched and the method returns immediately. The J2EE context
    *of the caller is used to execute the Work
    *@param Work -the work to execute.
	*@param WorkListener -which is used to inform the application of the progress of the work.
    *@returns WorkItem representing the asynchronous work.
    *@throws WorkException is queuing up results in an exception
    *@throws IllegalArgumentException -if work is a javax.ejb.EnterpriseBeam.
    */
	public WorkItem schedule(Work work, WorkListener wl) throws WorkException, IllegalArgumentException {
        
		if(work==null)
		return null;
		
		if (_logger.isDebugEnabled()) {
		    _logger.debug("inside schedule method");
	    }

		WorkItemImpl m_workItem = new WorkItemImpl(work, wl);
        WorkWrapper m_workWrapper = new WorkWrapper(work, wl, m_workItem);
        m_workWrapper.setWorkManager(this);
        this.add(m_workWrapper);

		if(!work.isDaemon()){
			try{
				if (_logger.isEnabledFor(Level.INFO)) {
			    	_logger.info("thread is non deamon");
				}

				this.start();
				if(thp != null) {
        			//Work_Accepted=1
					m_workItem.setStatus(WorkEvent.WORK_ACCEPTED,null);
					thp.submit(m_workWrapper);
				}

			}catch(IllegalStateException e2){
				if(_logger.isDebugEnabled())
				_logger.debug(e2.getMessage(), e2);
				WorkException wrkXception = new WorkException("queing up resulted an error");
				//WORK_REJECTED=2   
				((WorkItemImpl)m_workItem).setStatus(WorkEvent.WORK_REJECTED, wrkXception);
				throw wrkXception;
			} 
		}else{
			
			if (_logger.isEnabledFor(Level.INFO)) {
				_logger.info("thread is deamon");
			}
			
			if (maxDaemonThread == daemonThreadCount) {
				if(_logger.isDebugEnabled())
				_logger.debug("maximum daemon thread limit reached");
				throw new WorkException("maximum daemon thread limit reached"); 
			}
			
			else {
				DaemonThread dth = new DaemonThread(m_workWrapper);
				this.increaseDaemonCount();
		    	try {
        			//Work_Accepted=1
					m_workItem.setStatus(WorkEvent.WORK_ACCEPTED,null);
					dth.start();
				}catch(IllegalThreadStateException e) {
					_logger.error(e.getMessage(), e);
				 	WorkException wrkXception = new WorkException(e.getMessage());	
					//WORK_REJECTED=2   
				 	((WorkItemImpl)m_workItem).setStatus(WorkEvent.WORK_REJECTED, wrkXception);
				}
			}
        } 

        return (WorkItem)m_workItem;
	}


   /**Wait for all WorkItem in the collection to finish successfully or otherwise. WorkItems from differnt WorkManager 
    *can be placed in a single collection and waited on together.
	*The WorkItems collection should not be altered once submitted until the method returns.
	*@param workItems -the Collection of WorkItem object to wait for.
	*@param timeout_ms -the timeout in millliseconds. If this is 0 then method returns immediately
	*@returns true if all WorkItems have completed, false if the timeout has expired.
	*@throws InterruptedException - if wait is interrupted.
	*@throws IllegalArgumentException -if workItems is null, any of the objects in the collection are not WorkItem
	*or timeout_ms is negative.
	*/
	public boolean waitForAll(Collection workItems, long timeout_ms) throws InterruptedException, IllegalArgumentException {
		
			if (_logger.isDebugEnabled()) {
		        _logger.debug("inside waitForAll method");
		     }

			if(timeout_ms<0||workItems==null)
				throw new IllegalArgumentException("negative timeout or null collection");
			
			long timeout = timeout_ms;
			long start = System.currentTimeMillis();

			try{

				for(Iterator i = workItems.iterator(); i.hasNext(); ) {
					Object temp = i.next();
					
					if(temp instanceof WorkItem) {
						if(((WorkItem)temp).getStatus()==WorkEvent.WORK_REJECTED)   //workRejected
							return false;
						if(((WorkItem)temp).getStatus()==WorkEvent.WORK_COMPLETED)   //workCompleted
							continue;
						if(timeout_ms==WorkManager.IMMEDIATE) 
							return false;

						else{
								synchronized(temp){
									((WorkItem)temp).wait(timeout);
								}
								if((System.currentTimeMillis()-start)>=timeout_ms)
								break; 
								timeout = timeout-(System.currentTimeMillis()-start);
					 		}
					}else
						throw new IllegalArgumentException("some object in the collection is not workItems");
				}
				
			}catch(IllegalArgumentException e1){
			    _logger.error(e1.getMessage(), e1);
				return false;
		    }catch(IllegalMonitorStateException e2){
		        _logger.error(e2.getMessage(), e2);
				return false;
		    }catch(InterruptedException e3){
		        _logger.error(e3.getMessage(), e3);
		        throw new InterruptedException("thread is interruppted in waitForAll");
		    }catch(NoSuchElementException e4){
				_logger.error(e4.getMessage(), e4);
				return false;
			}

			if((timeout_ms == WorkManager.IMMEDIATE) || ((System.currentTimeMillis()-start)<timeout_ms))
				return true;
			else
				return false;
	}

							  
   /**
    * Wait till the timeout_ms. If there are no WorkItems in the list it returns immediately indicating a timeout.
	* WorkItems from different WorkManagers can be placed in a single collection and waited	on together.
	* The WorkItems collection should not be altered once submitted untill method returns
	* @param workItems Collection
	* @param timeout_ms long
	* @returns Collection -the WorkItems that have completed of an empty collection if time out expires before 
	* any finished.
	* @throws InterruptedException -if wait is interrupted
	o
	* @throws IllegalArgumentException -if workitems is null, any of the objects in the collection 
	* are not WorkItems or timeout_ms is negative.
    */
	public Collection waitForAny(Collection workItems, long timeout_ms)
							throws InterruptedException, IllegalArgumentException {
			
		if (_logger.isDebugEnabled()) {
		    _logger.debug("inside waitForAny method");
	    }

		if ((timeout_ms < 0) || (workItems == null))
		     throw new IllegalArgumentException("negative timeout or null collection");
		
		Collection completed_WI = new ArrayList();

		long timeout = timeout_ms;
		long start = System.currentTimeMillis();
		 
		try{
			for(Iterator i = workItems.iterator(); i.hasNext(); ) {
		 		Object temp = i.next();
				if(temp instanceof WorkItem){
					if(((WorkItem)temp).getStatus()==WorkEvent.WORK_REJECTED)   //workRejected
						continue;

					if(((WorkItem)temp).getStatus()==WorkEvent.WORK_COMPLETED) {  //workCompleted
						completed_WI.add((WorkItem)temp);
						continue;
					}

   					if ((timeout <= 0) || (timeout_ms == WorkManager.IMMEDIATE)) {
						continue;
					
					} else {
						synchronized(temp){
						if(_logger.isDebugEnabled())
						_logger.debug("hashCode of waiting WorkItem" + ((WorkItem)temp).hashCode());
							((WorkItem)temp).wait(timeout);
						}
						
						timeout = timeout-(System.currentTimeMillis()-start);

						if(((WorkItem)temp).getStatus()==WorkEvent.WORK_COMPLETED) {  //workCompleted
							completed_WI.add((WorkItem)temp);
						}
			    	}
				}else
					throw new IllegalArgumentException("some object in the collection is not workItems");
	    	}
		
		}catch(IllegalArgumentException e1){
			_logger.error(e1.getMessage(), e1);
	    }catch(IllegalMonitorStateException e2){
	    	_logger.error(e2.getMessage(), e2);
	    }catch(InterruptedException e3){
	    	_logger.error(e3.getMessage(), e3);
	    	throw new InterruptedException("thread is interruppted in waitForAll");
	    }catch(NoSuchElementException e4){
	    	_logger.error(e4.getMessage(), e4);
	    }

		return completed_WI;
	}

									

	private synchronized void add(WorkWrapper p_workWrapper) {
		wrapSet.add(p_workWrapper);					

	}
	
	
	public synchronized void remove(WorkWrapper p_workWrapper) {
		wrapSet.remove(p_workWrapper);

	}

	private synchronized void increaseDaemonCount() {
		if(this.daemonThreadCount < maxDaemonThread)
			this.daemonThreadCount++;
	}

	public synchronized void decreaseDaemonCount() {
		if(this.daemonThreadCount > 0)
			this.daemonThreadCount--;
	}

	public void initialize() {
		//reading value of thread pool from ase.properties
		this.wrapSet = new HashSet();
		this.config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String maxWmThread = null ;
		String maxQueueSize = null ;
		String maxDaemon = null;
		if (this.config != null) {
			maxWmThread = this.config.getValue(Constants.WORKMGR_THRDPOOL_SIZE);
			maxQueueSize = this.config.getValue(Constants.WORKMGR_MAXQUEUE_SIZE);
			maxDaemon = this.config.getValue(Constants.WORKMGR_DAEMON_COUNT);
		}
		
       	try{
			if (_logger.isDebugEnabled()) {
			    _logger.debug("for threadpool size value obtained from ase.properties is " +maxWmThread);
			    _logger.debug("for max queue size value obtained from ase.properties is " +maxQueueSize);
			    _logger.debug("for max daemon threads value obtained from ase.properties is " +maxDaemon);
			 }

			if(maxWmThread != null)
			 	threadPoolSize = Integer.parseInt(maxWmThread.trim());
                 
			if(maxQueueSize != null)
			 	queueSize = Integer.parseInt(maxQueueSize.trim());
                 
			if(maxDaemon != null)
			 	maxDaemonThread = Integer.parseInt(maxDaemon.trim());
             if (_logger.isDebugEnabled()) {   
			_logger.debug("threadPoolSize"+threadPoolSize); //testing
			_logger.debug("queueSize "+queueSize); //testing
			_logger.debug("maxDaemonThread "+maxDaemonThread); //testing
			}
		}catch(NumberFormatException e){
			_logger.error(e.getMessage(), e);
		}
	
		

		try{
			if (_logger.isDebugEnabled()) 
		    _logger.debug("threadPoolSize is" +threadPoolSize);
			thp=new ThreadPool(threadPoolSize, true, "WM_Thread", null, null, 50);
			thp.setFetchCount(1);
			ThreadMonitor tm = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
			thp.setThreadMonitor(tm);
			thp.setMaxQueueSize(queueSize);
			
		}catch(ThreadPoolException e1) {
	   		_logger.error(e1.getMessage(), e1);
		}
	}

	public void stop(){
		for(Iterator i = wrapSet.iterator(); i.hasNext(); ){
			WorkWrapper m_wrp = (WorkWrapper)i.next();
			m_wrp.release();
		}
		try{
			wrapSet.clear();
			if(thp != null) {
				thp.shutdown();
	    	}
			isPoolStarted = false;
			thp = null;
		}catch(UnsupportedOperationException e) {
			_logger.error(e.getMessage(), e);
		}
	}

	public synchronized void start() {
		if(!isPoolStarted){
			if (_logger.isDebugEnabled()) 
			_logger.debug("inside start method of WorkManagerImpl");
			if(thp == null) {
				_logger.debug("initialize called from start method of WorkManagerImpl");
				this.initialize();
			}
	    	if(thp != null){
				if (_logger.isDebugEnabled()) 
				_logger.debug("thread pool not equal to null");
				thp.start();
				if (_logger.isDebugEnabled()) {
			    _logger.debug("starting threadPool");
		    }
		}

	    	isPoolStarted = true;
		}
	}

   /**
    * Retrieves the Reference of this object.
	* @return Reference -the non null Reference of this object
	* @throws NamingException
	*/
	public Reference getReference() throws NamingException {
		if (_logger.isDebugEnabled()) {
		_logger.debug("Inside getReference() of WorkManagerImpl");
		}
		Reference ref = new Reference( 
			WorkManagerImpl.class.getName(), 
			new StringRefAddr("_workManager", _workManager),
			WorkManagerFactory.class.getName(), 
			null);
		ref.add(new StringRefAddr("_appId", m_appId));

		return ref;
	}

	public String toString() {
		return _workManager;
	}

		

/*		

	// for UT


	public static void main(String[] args) {

		Work w1 = new TestAppClass();
		//Work w2 = new TestAppClass();
		
		WorkListener wl1 = new TestAppClass();
		//WorkListener wl2 = new TestAppClass();
		WorkManager m_workManager=new WorkManagerImpl();
		((WorkManagerImpl)m_workManager).initialize();
		
		try{
		WorkItem w_Item1=m_workManager.schedule(w1, wl1);
		//WorkItem w_Item2=m_workManager.schedule(w2, wl2);
		Collection coll=new ArrayList();
		coll.add(w_Item1);
		//coll.add(w_Item2);
		Collection complete = m_workManager.waitForAny(coll, 20);
		System.out.println("check collection.isEmpty()" + complete.isEmpty());
		//for(Iterator i=complete.iterator(); i.hasNext();){
		//	Object temp=i.next();
		//	if(temp instanceof WorkItem)
		//	System.out.println("the completed works are" +((WorkItem)temp).hashCode());
		//}
		//System.out.println("value of the WorkItem returned is" + w_Item1.hashCode());
		//System.out.println("value of the WorkItem returned is" + w_Item2.hashCode());
		//System.out.println("checking the equality of the two WorkItems returned " + w_Item1.equals(w_Item2));
		}catch(WorkException e){
			_logger.error(e.getMessage(), e);
		}catch(InterruptedException e1){
		    _logger.error(e1.getMessage(),e1);
	    }catch(IllegalArgumentException e2){
	        _logger.error(e2.getMessage(),e2);
	    }catch(NoSuchElementException e3){
			_logger.error(e3.getMessage(), e3);
		}

		
		return;
	} */
 }

	/*
	class TestAppClass implements Work, WorkListener {
		private static Logger _logger = Logger.getLogger(TestAppClass.class);
		
		public boolean isDaemon() {
			return false;
		}

		public void release() {
			_logger.debug("release is called");
		}

		public void run() {
			_logger.debug("hello, every thing is fine");
			//for(int i=0;true;i++){
			//}
		}

		public void workAccepted(WorkEvent p_we) {
			_logger.debug("workAccepted");
			_logger.debug("this is for Work"+ (p_we.getWorkItem()).hashCode());
		}

		public void workCompleted(WorkEvent p_we) {
			_logger.debug("workCompleted");
		}

		public void workRejected(WorkEvent p_we) {
			_logger.debug("workRejected");
		}

		public void workStarted(WorkEvent p_we) {
			_logger.debug("workStarted");
		}
	} */




