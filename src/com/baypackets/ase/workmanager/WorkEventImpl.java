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
//		File:	WorkEventImpl.java
//
//		Desc:	This file implements commonj.work.WorkEvent interface
//
//		Author							Date			Description
//		----------------------------------------------------------------------
//		Somesh Kr. Srivastava			04/10/07		Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.workmanager;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;

/**
 * this is sent to workListener as work is processed by a WorkManager.
 * @author Somesh Kr. Srivastava
 */
public class WorkEventImpl implements WorkEvent {

	private final WorkItem m_workItem;
	private final WorkException m_exception;
	private final int m_type;

  	/**
	 * Constructor for workEvent
     * @param int type, workItem workException
	 */
	public WorkEventImpl(int p_type, WorkItem p_workItem, WorkException p_exception) {
		m_type = p_type;
		m_workItem = p_workItem;
		m_exception = p_exception;
	}

  	/**
     *@return m_type current status of the work
     */
	public int getType() {
		return m_type;
	}

  	/**
     * The WorkItem that this WorkEvent is for
	 * @return m_workItem 
     */
	public WorkItem getWorkItem() {
		return m_workItem;
	}


  	/**
     *The WorkException thrown if a Work is completed with exception
     *null if there is no exception
     */
	public WorkException getException() {
		return m_exception;
	}

}
