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
//      File:   WorkWrapper.java
//
//      Desc:   This file implements com.baypackets.ase.spi.util.Work interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           04/10/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.workmanager;

import commonj.work.Work;
import commonj.work.WorkListener;
import commonj.work.WorkItem;
import commonj.work.WorkEvent;
import commonj.work.WorkManager;
import commonj.work.WorkException;
import commonj.work.WorkCompletedException;

//import com.baypackets.ase.spi.util.*;
//import com.baypackets.ase.spi.util.WorkListener;

import java.lang.Exception;
import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * This wraps Work, WorkListener, WorkItem objects and 
 * submitted to the threadPool by the WorkManager
 *
 * @author Somesh Kr. Srivastava
 */
public class WorkWrapper implements com.baypackets.ase.spi.util.Work{

	private final commonj.work.Work m_work;
	private final commonj.work.WorkListener m_workListener;
	private WorkItem m_workItem;
	private WorkManager m_workmanager;
	private static Logger _logger = Logger.getLogger(WorkWrapper.class);

	public WorkWrapper(commonj.work.Work p_work, WorkListener p_workL, WorkItem p_workI){

		m_work=p_work;
		m_workListener=p_workL;
		m_workItem=p_workI;
	}

    /**
     * It calls the run() method of the commonj.work.Work
     * it also sets the status of the WorkItem 
     */
	public void execute() {
		try{
			if (_logger.isDebugEnabled()) {
		        _logger.debug("inside execute method of WorkWrapper");
		     }

			//WORK_STARTED=3
			((WorkItemImpl)m_workItem).setStatus(WorkEvent.WORK_STARTED, null);
			m_work.run();
			//WORK_COMPLETED=4	
			((WorkItemImpl)m_workItem).setStatus(WorkEvent.WORK_COMPLETED, null);
               
            synchronized(m_workItem){
	 			m_workItem.notifyAll();
				if(_logger.isDebugEnabled())
				_logger.debug("hashCode of the notified object is " +m_workItem.hashCode());
    		}
			if (_logger.isDebugEnabled()) {
			    _logger.debug("notify is sent to all waiting threads");
		    }

			if (m_work.isDaemon()) {
				((WorkManagerImpl)m_workmanager).decreaseDaemonCount();
			}
			
			((WorkManagerImpl)m_workmanager).remove(this);
	  
		}catch(Exception e) {
			//WORK_COMPLETED=2, with exception	
			if(_logger.isInfoEnabled())
			_logger.info("exception occured while run ",e);
			String expMess = e.getMessage();
			WorkCompletedException exp = new WorkCompletedException(expMess);
			((WorkItemImpl)m_workItem).setStatus(WorkEvent.WORK_COMPLETED, exp);
			if (m_work.isDaemon()) {
				((WorkManagerImpl)m_workmanager).decreaseDaemonCount();
			}

		}
	}
 
 
  
	public void setWorkManager(WorkManager p_workmanager){
		m_workmanager=p_workmanager;
	}

	
	/**
	 * this is called by the stop method in workManager
	 * it calls the release method on commonj.work.Work
	 */
	public void release(){
		m_work.release();
		if (m_work.isDaemon()) {
			((WorkManagerImpl)m_workmanager).decreaseDaemonCount();
		}
	}

	public com.baypackets.ase.spi.util.WorkListener getWorkListener(){
		return null;
	}
 
	public int getTimeout(){
		return 0;
	}

}

