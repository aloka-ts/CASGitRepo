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
//      File:   StackListener.java
//
//      Desc:   This class defines a listerner class for listeninig the callback
// 				from stack.This class implements ServerPDUEventListener class 
//				provided by stack as a callback listener.
//				SmppConfMgr class creates a new instance of this class
//				for every SmscSession and passes it to stack.All the callback 
//				messages coming inside that SmscSession are passed to this class
//				object associated with SmscSession.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              01/02/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.smpp.ServerPDUEventListener;
import org.smpp.WrongSessionStateException;

import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptor;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.ra.smpp.server.receiver.SMSCSession;
import com.baypackets.ase.ra.smpp.server.receiver.SmppPDUProcessor;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.stackif.*;
import com.baypackets.ase.resource.ResourceException;

import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.pdu.PDU;
import org.smpp.pdu.Response;
import org.smpp.pdu.ValueNotSetException;

public class StackListener implements ServerPDUEventListener {
	private static Logger logger = Logger.getLogger(StackListener.class);
    private SmscSession smscSession;
    private SmppResourceAdaptor ra;

    public StackListener(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside StackListener()");
		}
   		this.smscSession=smscSession;
		this.ra=SmppResourceAdaptorImpl.getInstance();
    }
   
   /**
    *	This is the callback method used by stack to pass all the incoming 
	*	Smpp messages coming inside this SmscSession from network.
	*
	*	@param event -Object of ServerPDUEvent.
	*/
    public void handleEvent(ServerPDUEvent event){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside handleEvent()");
		}
		if(!smscSession.isReceiver()){
			logger.error("Smsc is not binded as transmitter.");
		}
		try{
        	PDU pdu = event.getPDU();
			if(pdu != null){
        		if(pdu.isRequest()){
          		 	 deliverRequest(pdu);
        		} else if(pdu.isResponse()){
          		  deliverResponse(pdu);
        		} else {
           		    throw new Exception();
        		}
			}
		}catch(Exception ex){
			logger.error("Problem in delivering message to RA",ex); 
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving handleEvent()");
		}
    }

	/**
	 *	This method is  used by <code>StackListener</code> class to deliver
	 *	any <code>SmppRequest</code> coming from SMSC to SMPP resource adaptor.
	 *	This method also checks if the response should be delivered to resource
	 *	adaptor or not.
	 *
	 *	@param pdu	-PDU object form of incoming request.
	 *
	 *	@throws ResourceException -incase there is any problem in delivering 
	 *								the request.
	 */
    public void deliverRequest(PDU pdu) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside deliverRequest(PDU)");
		}
		boolean deliverUpward=true;
		SmppRequest request=null;
		SmscSession smscSession=null;
		SMSCSession smscSessionResponse=null;
		SmppPDUProcessor smppPDUProcessor=null;

        if(pdu instanceof org.smpp.pdu.Unbind){
            // TODO how to respond to this request
            // create a new UnbindResponse and send it on 
            // this session
            // and sent session state to unbound
            this.smscSession.setBound(false);
            this.smscSession.setCurrentStatus(Constants.STATUS_DOWN);
			deliverUpward=false;
			SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
													Constants.UNBIND_REQ,true);
        } else{
			if(!(this.smscSession.isReceiver())){
				logger.error("SmscSession is not binded as receiver.");
				logger.error("Discarding incoming request");
				return;
			}
			if(pdu instanceof org.smpp.pdu.DataSM){
				if(logger.isDebugEnabled()) {
					logger.debug("DataSM received");
				}
				request = new DataSM((org.smpp.pdu.DataSM)pdu);
				((DataSM)request).setSmscSession(this.smscSession);
				SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
														Constants.DATA_SM_REQ,true);
			}


			
			else if(pdu instanceof  org.smpp.pdu.SubmitSM){
				logger.debug("Submit Sm request found ");
				request = new SubmitSM();
				deliverUpward=true;
				((SubmitSM)request).setSmscSession(this.smscSession);
				SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
						Constants.SUBMIT_SM_REQ,true);
			}

			else if(pdu instanceof org.smpp.pdu.DeliverSM){
				if(logger.isDebugEnabled()) {
					logger.debug("DeliverSM received");
				}
				request = new DeliverSM((org.smpp.pdu.DeliverSM)pdu);
				
				((DeliverSM)request).setSmscSession(this.smscSession);
				SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
														Constants.DELIVER_SM_REQ,true);
			} else if(pdu instanceof org.smpp.pdu.EnquireLink){
				if(logger.isDebugEnabled()) {
					logger.debug("EnquireSM received");
				}
				deliverUpward=false;
				this.smscSession.setCurrentStatus(Constants.STATUS_ACTIVE);
				
				logger.debug("Sending response for enquireSM");
				Response response=((org.smpp.pdu.EnquireLink) pdu).getResponse();
				response.setCommandStatus(Data.ESME_ROK);
				try {
					this.smscSession.getStackObject().respond(response);
				} catch (Exception e) {
				   logger.error("Error in sending enquire link response in Stacklistener:-"+ e);	
				}
				
				SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
														Constants.ENQUIRE_LINK_REQ,true);
				// TODO
			} else if(pdu instanceof org.smpp.pdu.AlertNotification){
				if(logger.isDebugEnabled()) {
					logger.debug("AlertNotification received");
				}
				request = new AlertNotification((org.smpp.pdu.AlertNotification)pdu);
				((AlertNotification)request).setSmscSession(this.smscSession);
				SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
														Constants.ALERT_NOTIFICATION,true);
			} else if(pdu instanceof org.smpp.pdu.GenericNack){
				if(logger.isDebugEnabled()) {
					logger.debug("Generic Nack received");
				}
				deliverUpward=false;
				SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
														Constants.GENERIC_NACK,true);
				// TODO
			}else{
				deliverUpward=false;
			}

			if(deliverUpward){
				logger.debug("Sending Request UP as delivery SM found");
            	ra.deliverRequest(request);
			}
        }
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving deliverRequest(PDU)");
		}
    }
                   
	/**
	 *	This method is  used by <code>StackListener</code> class to deliver
	 *	any <code>SmppResponse</code> coming from SMSC to SMPP resource adaptor.
	 *	This method alse checks if the response should be delivered to resource
	 *	adaptor or not.
	 *
	 *	@param pdu	-PDU object form of incoming response.
	 *
	 *	@throws ResourceException -incase there is any problem in delivering 
	 *								the response.
	 */
    public void deliverResponse(PDU pdu) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside deliverResponse(PDU)");
		}
		boolean deliverUpward=true;
		SmppResponse response=null;

        if(pdu instanceof org.smpp.pdu.BindResponse){
			deliverUpward=false;
			if(pdu.getCommandStatus()!=Constants.ESME_ROK){
				// TODO probelem in making connection with SMSC
			}else{
            	this.smscSession.setBound(true);
			}
        	if(pdu instanceof org.smpp.pdu.BindReceiverResp){
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
											Constants.BIND_RECEIVER_RES,true);	
			}else if(pdu instanceof org.smpp.pdu.BindTransmitterResp){
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
											Constants.BIND_TRANSMITTER_RES,true);
			}else if(pdu instanceof org.smpp.pdu.BindTranscieverResp){
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
											Constants.BIND_TRANSCEIVER_RES,true);
			}
        } else{
        	if(pdu instanceof org.smpp.pdu.SubmitSMResp){
				if(logger.isDebugEnabled()) {
					logger.debug("Submit response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.SUBMIT_SM_RES,true);
				response = new SubmitSMResp((org.smpp.pdu.SubmitSMResp)pdu);
			}else if(pdu instanceof org.smpp.pdu.SubmitMultiSMResp){
				if(logger.isDebugEnabled()) {
					logger.debug("Submit multi response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.SUBMIT_SM_MULTI_RES,true);
				response = new SubmitMultiSMResp((org.smpp.pdu.SubmitMultiSMResp)pdu);
			}else if(pdu instanceof org.smpp.pdu.DataSMResp){
				if(logger.isDebugEnabled()) {
					logger.debug("data response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.DATA_SM_RES,true);
				response = new DataSMResp((org.smpp.pdu.DataSMResp)pdu);
			}else if(pdu instanceof org.smpp.pdu.CancelSMResp){
				if(logger.isDebugEnabled()) {
					logger.debug("cancel response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.CANCEL_SM_RES,true);
				response = new CancelSMResp((org.smpp.pdu.CancelSMResp)pdu);
			}else if(pdu instanceof org.smpp.pdu.ReplaceSMResp){
				if(logger.isDebugEnabled()) {
					logger.debug("Replace response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.REPLACE_SM_RES,true);
				response = new ReplaceSMResp((org.smpp.pdu.ReplaceSMResp)pdu);
			}else if(pdu instanceof org.smpp.pdu.QuerySMResp){
				if(logger.isDebugEnabled()) {
					logger.debug("query response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.QUERY_SM_RES,true);
				response = new QuerySMResp((org.smpp.pdu.QuerySMResp)pdu);
			}else if(pdu instanceof org.smpp.pdu.EnquireLinkResp){
				if(logger.isDebugEnabled()) {
					logger.debug("Enquire link response received");
				}
				SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
													Constants.ENQUIRE_LINK_RES,true);
				deliverUpward=false;
				// TODO
			} else{
				deliverUpward=false;
			}

			if(deliverUpward){
				if(logger.isDebugEnabled()) {
					logger.debug("delivering message to RA");
				}
           			ra.deliverResponse(response);
			}
        }
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving deliverResponse(PDU)");
		}
    }
}
                   
