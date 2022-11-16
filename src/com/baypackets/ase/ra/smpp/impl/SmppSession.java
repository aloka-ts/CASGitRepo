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
//      File:   SmppSession.java
//
//      Desc:   This class defines Smpp specific resource session.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              01/02/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.stackif.AbstractSmppRequest;
import com.baypackets.ase.ra.smpp.stackif.DataSM;
import com.baypackets.ase.ra.smpp.stackif.SubmitMultiSM;
import com.baypackets.ase.ra.smpp.stackif.SubmitSM;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;

public class SmppSession extends AbstractSession {

	private static Logger logger = Logger.getLogger(SmppSession.class);
	private boolean isReadyForReplication = true;
	private ArrayList requests = new ArrayList(1);
	private ArrayList rgstdRequests = new ArrayList(1);
	private String messageId=null;
	private int sequenceNo=-1;

	public SmppSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside Empty SmppSession Constructor.........");
		}
		
	}
	public SmppSession(String id) {
		
		super(id);
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SmppSession Constructor.........id is" +id);
		}
		
	}
	
	public Message createMessage(int type) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createMessage(int)");
		}
		//return (Message)mf.createRequest(this, type);
		return null;
	}

	public String getProtocol() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocol()");
		}
		//return Constants.PROTOCOL;
		return null;
	}

	public void associateMessageId(String msgId,int seqNo){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside associateMessageId() with messageId="
							+msgId +" and sequence no=" +seqNo);
		}
		this.messageId=msgId;
		this.sequenceNo=seqNo;
		this.setModified(true);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving associateMessageId()");
		}
	}
	public void readMessageId(String msgId,int seqNo){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside readMessageId()");
		}
		try{
			for(int i=0;i<this.requests.size();i++){
				SmppRequest request=(SmppRequest)this.requests.get(i);
				if(request.getSequenceNumber()==seqNo){
					if(logger.isDebugEnabled()) {
						logger.debug("sequence no matched.");
					}
					// setting message id into request
					if(request instanceof SubmitSM){
						((SubmitSM)request).setMessageId(msgId);
					}else if(request instanceof SubmitMultiSM){
						((SubmitMultiSM)request).setMessageId(msgId);
					}else if(request instanceof DataSM){
						((DataSM)request).setMessageId(msgId);
					}
					this.requests.remove(request);
					this.rgstdRequests.add(request);
				}
			}
		}catch(Exception ex){
			logger.error("Exception in readMessage id "+ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving readMessageId()");
		}
	}
	
	public void addRequest(SmppRequest request ){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
		if(request != null && this.requests.indexOf(request) == -1) {
			logger.debug("adding the request to list " + request);
			this.requests.add(request);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving addRequest()");
		}
	}
	
	public void cleanup(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside cleanup() of "+this);
		}
		for(int i=0;i<this.requests.size();i++){
			SmppRequest request=(SmppRequest)this.requests.get(i);
			int index=requests.indexOf(request);
			if(index!=-1){
				if(logger.isDebugEnabled()) {
					logger.debug("Removing request "+request);
				}
				requests.remove(index);
			}
			if(logger.isDebugEnabled()) {
				logger.debug("removing request from map "+request);
			}
			SmppResourceAdaptorImpl.getInstance().
									removeRequestFromMap(request);
		}
		for(int i=0;i<this.rgstdRequests.size();i++){
			SmppRequest request=(SmppRequest)this.rgstdRequests.get(i);
			int index=rgstdRequests.indexOf(request);
			if(index!=-1){
				if(logger.isDebugEnabled()) {
					logger.debug("Removing request "+request);
				}
				rgstdRequests.remove(index);
			}
			if(logger.isDebugEnabled()) {
				logger.debug("removing request from rgstd map "+request);
			}
			SmppResourceAdaptorImpl.getInstance().
									removeRgstdRequestFromMap(request);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving cleanup()");
		}
	}

	///////////// Replicable Interface method ////////////////////

	// This method adds the request attached with this session into
	// the hashMap of stackInterfacelayer.

	public void partialActivate(ReplicationSet parent) {	
		if(logger.isDebugEnabled()) {
			logger.debug("Inside partialActivate() of "+this);
		}
		int seqNum=-1;
		String messageId=null;
		for(int i=0;i<this.requests.size();i++){
			SmppRequest request=(SmppRequest)this.requests.get(i);
			try{
				seqNum=request.getSequenceNumber();
			}catch(Exception ex){
				logger.error("Exception in getting sequence number",ex);
			}
			if(seqNum!=-1){
				if(logger.isDebugEnabled()) {
					logger.debug("Adding request to map "+request);
				}
				SmppResourceAdaptorImpl.getInstance().
										addRequestToMap(seqNum,request);
			}
			((AbstractSmppRequest)request).setProtocolSession(this);
		}
		for(int i=0;i<this.rgstdRequests.size();i++){
			SmppRequest request=(SmppRequest)this.rgstdRequests.get(i);
			try{
				messageId=request.getMessageId();
			}catch(Exception ex){
				logger.error("Exception in getting message id ",ex);
			}
			if(messageId!=null){
				if(logger.isDebugEnabled()) {
					logger.debug("Adding request to rgstd map "+request);
				}
				SmppResourceAdaptorImpl.getInstance().
										addRgstdRequestToMap(messageId,request);
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Calling partialActivate on super");
		}
		super.partialActivate(parent);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving partialActivate()");
		}
	}
 
	public boolean isReadyForReplication() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isReadyForReplication()");
		}
		return isReadyForReplication;
	}

	public void replicationCompleted() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside replicationCompleted() of "+this);
		}
		replicationCompleted(false);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving replicationCompleted() of "+this);
		}
	}
	
	public void replicationCompleted(boolean noReplication) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside replicationCompleted() of "+this+" NoRep"+noReplication);
		}
		super.replicationCompleted(noReplication);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving replicationCompleted() of "+this+" NoRep"+noReplication);
		}
	}

	public void writeIncremental(ObjectOutput out, int replicationType) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside writeIncremental() of "+this);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Message id is "+this.messageId +
						" and sequence no is " +this.sequenceNo);
		}
		try{
			out.writeObject(this.messageId);
			out.writeInt(this.sequenceNo);
			super.writeIncremental(out, replicationType);
		}catch(Exception ex){
			logger.error("Exception in writeIncremental. "+ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving writeIncremental()");
		}
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside readIncremental() of "+this);
		}
		this.messageId=(String)in.readObject();
		this.sequenceNo=in.readInt();
		if(logger.isDebugEnabled()) {
			logger.debug("Message id is "+this.messageId +
						" and sequence no is " +this.sequenceNo);
		}
		readMessageId(messageId,sequenceNo);
		super.readIncremental(in);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving readIncremental()");
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside writeExternal() of "+this);
		}
		out.writeObject(this.requests);
		super.writeExternal(out);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving writeExternal()");
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside readExternal() of "+this);
		}
		this.requests = (ArrayList)in.readObject();
		super.readExternal(in);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving SmppSession readExternal()");
		}
	}
}
