/*******************************************************************************
 *   Copyright (c) Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/


/***********************************************************************************
//
//      File:   SmscPollingThread.java
//
//      Desc:   This class defines a thread which manages Smsc session with
///				all the SMSCc configured in smpp-config.xml It binds smsc
//				session with these SMSCs after initialization of the SMPP RA,
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              06/03/08        Initial Creation
//
/***********************************************************************************/




package com.baypackets.ase.ra.smpp.utils;

import java.util.List;
import java.util.Iterator;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.smpp.stackif.Constants;
import com.baypackets.ase.ra.smpp.stackif.SmscSession;
import com.baypackets.ase.util.AseUtils;

import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


public class SmscPollingThread implements Runnable {
	private static Logger logger = Logger.getLogger(SmscPollingThread.class);
	private List m_smscList; 
	Thread poolingThread;
	private boolean keepRunning=false;
	private int waitingTime=1000;

	public SmscPollingThread(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside default constructor");
		}
	}

	public void startPolling(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside startPolling()");
		}
		waitingTime=waitingTime*(Constants.POOLING_INTERVAL);
		if(logger.isDebugEnabled()) {
			logger.debug("waiting time is "+waitingTime);
		}
		this.keepRunning=true;
		poolingThread=new Thread(this,"SMSC-Pooling");
		poolingThread.start();
		if(logger.isDebugEnabled()) {
			logger.debug("poling thread started()");
		}
	}
	
	public void stopPolling(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside stopPolling()");
		}
		SmscSession smscSession;
		this.keepRunning=false;
		int size=m_smscList.size();
		if(size==0){
			logger.error("No smsc configured. Returning");
			return;
		}
		Iterator itr=m_smscList.iterator();
		while(itr.hasNext()){
			smscSession =(SmscSession)itr.next();
			smscSession.closeConnection();	
		} // while ends
	}

	/** 
	 *	This method returns the list of all the SMSC server configured
	 *	with SMPP RA in smpp-config.xml.
	 *
	 *	@return List -list of all the SMSCs configured with the RA. 
	 */
	public List getSmscList() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscList()");
		}
		 return m_smscList ;
	}

	/** 
	 *	This method sets the list of SMSC servers configured
	 *	with SMPP RA in smpp-config.xml.
	 *
	 *	@param list -List of all the SMSCs configured with the RA. 
	 */
	public void setSmscList( List list) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscList()");
		}
		m_smscList = list ; 
	}

	/**
	 *	This method is used aftre SMPP RA initialization to bind 
	 *	any SMSC if that is not in bound.For every SMSC in smsclist 
	 *	this method calls createSmscSession on that <code>SmscSessionM</code>
	 *	if the <code>SmscSession</code> is not in bound state.
	 *	
	 *	@param ra <code>SmppResourceAdaptor</code> object
	 */
		public void run(){
			if(logger.isDebugEnabled()) {
				logger.debug("Inside run()");
			}
			SmscSession smscSession=null;
			ConfigRepository repository = BaseContext.getConfigRepository();
			String sasFIPHost = repository.getValue(
							com.baypackets.ase.util.Constants.OID_SIP_FLOATING_IP);
			String sasFIP=AseUtils.getIPAddressList(sasFIPHost,true);
			logger.debug("TESTPK  FIP="+sasFIP); 
			String portVal= repository.getValue(
						com.baypackets.ase.util.Constants.PROP_SMPP_CONN_PORT);
			logger.debug("TESTPK  string port ="+portVal); 
			int port = Integer.parseInt(portVal);
			logger.debug("TESTPK  port ="+port); 
			
			Iterator itr = m_smscList.iterator();
			while(itr.hasNext()){
				smscSession =(SmscSession)itr.next();
				smscSession.setSasFIP(sasFIP);
				smscSession.setSasPort(port);
				port++;
			} 
			try{
				while(keepRunning){
					int size=m_smscList.size();
					if(size==0){
						logger.error("No smsc configured. Returning");
						return;
					}
					itr=m_smscList.iterator();
					while(itr.hasNext()){
						smscSession =(SmscSession)itr.next();
						smscSession.makeSmscConnection();	
					} // inner while ends
					Thread.sleep(waitingTime);
				} // while(keepRunning) ends
			}catch(Exception ex){
				logger.error("Interrupted");
			}
			if(logger.isDebugEnabled()) {
				logger.debug("Leaving run()");
			}
		}
}
