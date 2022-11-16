package com.baypackets.ase.ra.diameter.sh.stackif;


import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.sh.ShProfileUpdateRequest;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.ShResponse;
import com.baypackets.ase.ra.diameter.sh.impl.ShMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import org.apache.log4j.Logger;

public class ShProfileUpdateRequestImpl extends ShAbstractRequest implements ShProfileUpdateRequest {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ShProfileUpdateRequestImpl.class);

	private ShSession m_roSession;
	private int retryCounter=0;
	private DiameterSession serverStackSession;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterShMessageFactory diameterShMsgFactory;
	private DiameterStack stack=null;

	public ShProfileUpdateRequestImpl(int type){
		super(type);
		stack=((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack();
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
		logger.debug("Inside ShProfileUpdateRequestImpl constructor ");
	}

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


	//	public ShProfileUpdateRequestImpl(int type){
	//		super(type);
	//		// TODO change constructor
	//		stackObj = new MessageUserDataRequest(null,null);
	//		super.setStackObject(stackObj);
	//	}

	public ShProfileUpdateRequestImpl(ShSession shSession, int type, String remoteRealm){
		super(shSession);
		logger.debug("Inside ShProfileUpdateRequestImpl(ShSession) constructor ");
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
		try {
			this.stackObj = ShMessageFactoryImpl.createPUR(remoteRealm);
		} catch (DiameterException e) {
			logger.debug("Exception in creting UDR",e);
		}
		logger.debug("Stack object created ="+this.stackObj);
		super.setStackObject(this.stackObj);
		//this.m_shSession=shSession;
	}

	public void addRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
		//this.m_shSession.addRequest(this);
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
	public byte[] getRawUserData() throws ShResourceException {
		return new byte[0];
	}

	@Override
	public String getUserData() throws ShResourceException {
		return null;
	}

}
