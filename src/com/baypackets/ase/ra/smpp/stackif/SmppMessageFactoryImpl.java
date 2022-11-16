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
//      File:   SmppMessageFactoryImpl.java
//
//		Desc:	This class implements Resource adaptor message factory interface for SMPP. It 
//				provides various API to be used by application to create various 
//				smpp messages.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Random;
import java.util.ArrayList;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.ra.smpp.WrongMultipleDestException;
import com.baypackets.ase.ra.smpp.impl.SmppSession;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.ra.smpp.stackif.AddressImpl;
import com.baypackets.ase.ra.smpp.utils.SmppConfMgr;
import com.baypackets.ase.ra.smpp.stackif.SmscSession;

import org.smpp.pdu.DestinationAddress;

public class SmppMessageFactoryImpl implements SmppMessageFactory {
	private static Logger logger = Logger.getLogger(SmppMessageFactoryImpl.class);

	private static final int SUBMITSM=1;
	private static final int SUBMITMULTI=2;
	private static final int DATASM=3;

	private ResourceContext context;
	private MessageFactory msgFactory;
	private static SmppMessageFactoryImpl smppMsgFactory;

	
	/**
	 *	Default constructor for creating SmppMessageFactory object.
	 *
	 */
	public SmppMessageFactoryImpl(){
			logger.debug("creating SmppMessageFactory object");
			smppMsgFactory=this;
	}

	/**
	 *	This  method returns the instance of SmppMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static SmppMessageFactory getInstance(){
		if(smppMsgFactory==null){
			smppMsgFactory = new SmppMessageFactoryImpl();
		}
		return smppMsgFactory;
	}
	
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
		this.context = context;
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}
	}

	public SasMessage createRequest(SasProtocolSession session, int type)
		throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createRequest(SasProtocolSession)");
		}
		SmppRequest request;
		int seqNum = SmppResourceAdaptorImpl.getInstance().getNextSeqNumber();
		switch(type) {
			case 1:
			request = new SubmitSM((SmppSession)session);
			((SubmitSM)request).setSequenceNumber(seqNum);
			((SubmitSM)request).addRequest();
			((SubmitSM)request).setMessageMode((byte)Constants.SM_ESM_DEFAULT);
			break;
			case 2:
			request = new SubmitMultiSM((SmppSession)session);
			((SubmitMultiSM)request).setSequenceNumber(seqNum);
			((SubmitMultiSM)request).addRequest();
			((SubmitMultiSM)request).setMessageMode((byte)Constants.SM_DATAGRAM_MODE);
			break;
			case 3:
			request = new DataSM((SmppSession)session);
			((DataSM)request).setSequenceNumber(seqNum);
			((DataSM)request).addRequest();
			((DataSM)request).setMessageMode((byte)Constants.SM_DATAGRAM_MODE);
			break;
			default:
			throw new SmppResourceException("unknown type");
		}
		return (SasMessage)request;
	}

	public SasMessage createResponse(SasMessage request, int type) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createResponse(SasMessage)");
		}
			throw new SmppResourceException("Not allowed for Smpp");
	}

	public SmppRequest createRequest(SasProtocolSession session, Address sourceAddr, Address destAddr)
		throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createRequest(SasProtocolSession,Address,Address)");
		}
		SubmitSM request = (SubmitSM)this.msgFactory.createRequest(session,SUBMITSM);
		request.setSourceAddr(sourceAddr);
		request.setDestAddr(destAddr);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving createRequest(SasProtocolSession,Address,Address)");
		}
		return request;
	}
	
	public SmppRequest createRequest(SasProtocolSession session, Address sourceAddr, Address[] destAddr) 
								throws ResourceException,WrongMultipleDestException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createRequest(SasProtocolSession,Address,Address[])");
		}
		SubmitMultiSM request = (SubmitMultiSM)this.msgFactory.createRequest(session,SUBMITMULTI);
		request.setSourceAddr(sourceAddr);
		// checking if all the SMS are destined to one SMSC or not.
		// if not then throw WrongMultipleDestinations Exception.
		SmscSession smscSession=null;
		ArrayList newList=null;
		ArrayList matchedList=null;
		ArrayList newMatchedList=new ArrayList();
		// TODO check if it does not return null refeence
		SmppConfMgr confMgr = SmppResourceAdaptorImpl.getInstance().getConfigMgr();
		//SmppConfMgr confMgr = smppRA.getConfigMgr();
		if(confMgr==null){
			logger.debug("config manager is null ");
		}else{
			logger.debug("config manager is "+confMgr);
		}

		for(int count=0;count<destAddr.length;count++){
			logger.debug("inside begin for loop with count="+count); 
			newList = confMgr.getMatchingSmsc((AddressImpl)destAddr[count]);	
			if(count>0){
				for(int i=0;i<matchedList.size();i++){
					logger.debug("inside first for loop with i="+i+" and size="+matchedList.size());
					SmscSession matchedSession = (SmscSession)matchedList.get(i);
					logger.debug("Matched smsc session is "+matchedSession);
					for(int j=0;j<newList.size();j++){
						logger.debug("inside 2nd for loop with j="+j+" and size="+newList.size());
						SmscSession matchingSession = (SmscSession)newList.get(j);
						logger.debug("Matching smsc session is "+matchingSession);
						if(matchedSession==matchingSession){
							logger.debug("smsc session matched");
							newMatchedList.add(matchedSession);
						}
						logger.debug("leaving 2nd for loop with j="+j+" and size="+newList.size());
					} // for newList ends
					logger.debug("Leaving first for loop with i="+i+" and size="+matchedList.size());
				} // for matchedList ends
				//matchedList=newMatchedList;
				matchedList.addAll(newMatchedList);
				newMatchedList.clear();
			}else{
				matchedList=newList;
			}
			if(matchedList==null || matchedList.size()==0){
				throw new WrongMultipleDestException("No maching SMSC for all the destination address");
			}
		}
		// checking if all the SMSCs are in up state.
		ArrayList finalList = new ArrayList();
		for(int i=0;i<matchedList.size();i++){
			SmscSession tmpSession = (SmscSession)matchedList.get(i);
			boolean isTransmitter=tmpSession.isTransmitter();
			if(tmpSession.isBound() && isTransmitter &&StringUtils.equalsIgnoreCase(smscSession.getCurrentStatus(), Constants.STATUS_ACTIVE)){
				finalList.add(tmpSession);
			}
		}
		if(finalList.size()==0){
			logger.error("None of the SMSCs is in binded or transmitter state.");
			throw new ResourceException("None of the SMSCs is in binded or transmiter state");
		}
		try{
			Random rand = new Random();
			int index=rand.nextInt(finalList.size());
			smscSession = (SmscSession)finalList.get(index);
		}catch(Exception ex){
			throw new ResourceException("Problem in getting SMSC");
		}
		// setting Smsc session to avoid searching for the smsc session again
		request.setSmscSession(smscSession);

		for(int i=0;i<destAddr.length;i++){
			//org.smpp.pdu.DestinationAddress destAdd = 
			//	new org.smpp.pdu.DestinationAddress((AddressImpl)destAddr[i].getStackObject());
			request.addDestAddress((AddressImpl)destAddr[i]);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving createRequest(SasProtocolSession,Address,Address[])");
		}
		return request;
	}

	public SmppRequest createDataRequest(SasProtocolSession session, Address sourceAddr, Address destAddr)
		throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createDataRequest(SasProtocolSession,Address,Address)");
		}
		DataSM request = (DataSM)this.msgFactory.createRequest(session,DATASM);
		request.setSourceAddr(sourceAddr);
		request.setDestAddr(destAddr);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving createDataRequest(SasProtocolSession,Address,Address)");
		}
		return request;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type, String remoteRealm) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm, String msisdn) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
}
