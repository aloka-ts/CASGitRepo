package com.baypackets.ase.ra.diameter.sh.stackif;


import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.ShResponse;
import com.baypackets.ase.ra.diameter.sh.ShSubscribeNotificationsRequest;
import com.baypackets.ase.ra.diameter.sh.impl.ShMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import org.apache.log4j.Logger;

import java.util.Date;

public class ShSubscribeNotificationsRequestImpl extends ShAbstractRequest implements ShSubscribeNotificationsRequest {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ShSubscribeNotificationsRequestImpl.class);

	private ShSession m_ShSession;
	private int retryCounter=0;
	private DiameterSession serverStackSession;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterShMessageFactory diameterShMsgFactory;
	private DiameterStack stack=null;

	public void setStackObj(DiameterMessage stkObj){
		super.setStackObject(stkObj);
	}

	public DiameterMessage getStackObj(){
		return stackObj;
	}

	public DiameterSession getServerStackSession() {
		return serverStackSession;
	}

	public void setServerStackSession(DiameterSession stackServerSession) {
		this.serverStackSession = stackServerSession;
	}

	public void incrementRetryCounter() {
		this.retryCounter++;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public ShSubscribeNotificationsRequestImpl(ShSession shSession, int type, String remoteRealm,String msisdn){
		super(shSession);
		logger.debug("Inside ShSubscribeNotificationsRequestImpl(ShSession) constructor ");
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
		try {
			this.stackObj = ShMessageFactoryImpl.createUDR(remoteRealm,msisdn);
		} catch (DiameterException e) {
			logger.debug("Exception in creting UDR",e);
		}
		logger.debug("Stack object created ="+this.stackObj);
		super.setStackObject(this.stackObj);
	}

	public void addRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
	}

	////////////////////////////////////////////////////////////////////
	///////////////////////// OPENBLOX COMMON API STARTS ///////////////
	////////////////////////////////////////////////////////////////////

	@Override
	public Response createResponse(int arg0) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside createResponse(int). not implemented");
		}
		// TODO Auto-generated method stub
		return null;
	}

	////////////////////////////////////////////////////////////////////
	////////////// ShUserDataRequest Marben Specific API Starts ////////
	////////////////////////////////////////////////////////////////////

	@Override
	public byte getVersion() {
		return 0;
	}

	public java.lang.String toString(){
		// TODO
		return "ShUserDataRequest@"+this.hashCode();
	}

	@Override
	public byte[] getByteArray() {
		return new byte[0];
	}

	@Override
	public int getMessageLength() {
		return 0;
	}

	@Override
	public StandardEnum getStandard() {
		return null;
	}

	@Override
	public void toXML(StringBuilder builder) {

	}

	@Override
	public ValidationRecord validate() {
		return null;
	}

	@Override
	public int[] getDataReferences() throws ShResourceException {
		return new int[0];
	}

	@Override
	public String[] getDSAITags() throws ShResourceException {
		return new String[0];
	}

	@Override
	public Date getExpiryTime() throws ShResourceException {
		return null;
	}

	@Override
	public int[] getIdentitySets() throws ShResourceException {
		return new int[0];
	}

	@Override
	public int getOneTimeNotification() throws ShResourceException {
		return 0;
	}

	@Override
	public int getSendDataIndication() throws ShResourceException {
		return 0;
	}

	@Override
	public int getSubsReqType() throws ShResourceException {
		return 0;
	}
}
