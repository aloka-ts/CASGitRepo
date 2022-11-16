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
//      File:   DaemonThread.java
//
//      Desc:   This file handles deamon thread execution 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           04/10/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.workmanager;

import com.baypackets.ase.workmanager.WorkWrapper;
import java.lang.Thread;

/**
 * This handles daemon thread execution 
 *
 * @author Somesh Kr. Srivastava
 */
public class DaemonThread extends Thread {

		private WorkWrapper m_wrapper;

		public DaemonThread(WorkWrapper p_ww) {
			m_wrapper=p_ww;
		}

		public void run() {
			m_wrapper.execute();
		}
}
