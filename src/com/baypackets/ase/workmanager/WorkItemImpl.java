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
//      File:   WorkItemImpl.java
//
//      Desc:   This file implements commonj.work.WorkItem interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           04/10/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.workmanager;

import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkEvent;
import commonj.work.Work;
import commonj.work.WorkListener;

/**
 * This is returned once a Work is submitted to a WorkManager. It can be used to check the status of Work
 * after it's finished and to check any exception that it threw.
 * @author Somesh Kr. Srivastava
 */

public class WorkItemImpl implements WorkItem {

	private volatile int m_status;
	private Work m_work;
	private WorkListener m_workListener;
	private WorkException m_exception;

  
	/**
	 * Constructor for WorkItemImpl
	 * @param p_work -Work 
	 * @param p_wl -WorkListener
	 */
	public WorkItemImpl(Work p_work, WorkListener p_wl) {
		m_work=p_work;
		m_workListener=p_wl;
	}
 
   /**
    * Returns the completed Work or null if the Work is not yet complete
    * @throws WorkException if occured during run;
    * @return Work
    */
  	public Work getResult() throws WorkException {
		Work work = null;
		if(this.getStatus()==WorkEvent.WORK_REJECTED)
			throw m_exception; 
		
		if((this.getStatus()==WorkEvent.WORK_ACCEPTED)||(this.getStatus()==WorkEvent.WORK_STARTED))
			work = null;

		if(this.getStatus()==WorkEvent.WORK_COMPLETED) {
			if(m_exception != null) {
				throw m_exception;
			}
			work = m_work;
		}
		return work;
    }

  
   /**
    *Sets the Status of the WorkItem as WorkEvent.WORK_COMPLETED etc.
    *at the sametime it also calls back to WorkListener if it was provide
    *by the application
    *@param p_status int 
	*@param p_exception -WorkException
    */
  	public synchronized void setStatus(final int p_status, final WorkException p_exception){

		m_status=p_status;
		m_exception=p_exception;

		if(m_workListener !=null){
     			switch(p_status) {

				case WorkEvent.WORK_ACCEPTED:
					m_workListener.workAccepted(
   					new WorkEventImpl(WorkEvent.WORK_ACCEPTED, this, p_exception));
					break;

				case WorkEvent.WORK_REJECTED:
					m_workListener.workRejected(
     				new WorkEventImpl(WorkEvent.WORK_REJECTED, this, p_exception));
					break;
	
				case WorkEvent.WORK_COMPLETED:
					m_workListener.workCompleted(
     				new WorkEventImpl(WorkEvent.WORK_COMPLETED, this, p_exception));
					break;

				case WorkEvent.WORK_STARTED:
					m_workListener.workStarted(
     				new WorkEventImpl(WorkEvent.WORK_STARTED, this, p_exception));
					break;

        		}
       		}
   }
  
   /**
    * @return m_status current status of the work
	*/
  	public synchronized int getStatus(){
		return m_status;
	}
  
   /**@return m_work the work to which
    * this WorkItem is associated
	*/
  	private Work getWork(){
		return m_work;
		}
		

	public int compareTo(java.lang.Object wi) {
		int i=0;
		if(!(wi instanceof WorkItem))
			throw new ClassCastException("class cast exception");
				
		if(this.hashCode()==wi.hashCode())
			i= 0;
		if(this.hashCode()<wi.hashCode())
			i= 1;
		if(this.hashCode()>wi.hashCode())
			i= -1;
		return i;
			
	}
		
	public int hashCode() {
		return this.getWork().hashCode()*2+10;
	}

	public boolean equals(WorkItem wi) {
		if(this.hashCode()==wi.hashCode())
		return true;
		
		else
		return false;
	}

}

