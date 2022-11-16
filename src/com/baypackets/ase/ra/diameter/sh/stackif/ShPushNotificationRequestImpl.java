package com.baypackets.ase.ra.diameter.sh.stackif;


import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.sh.ShPushNotificationRequest;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import fr.marben.diameter.DiameterMessage;
import fr.marben.diameter.DiameterMessageFactory;
import fr.marben.diameter.DiameterSession;
import fr.marben.diameter.DiameterStack;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import org.apache.log4j.Logger;

public class ShPushNotificationRequestImpl extends ShAbstractRequest implements ShPushNotificationRequest {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ShPushNotificationRequestImpl.class);

	private ShSession m_ShSession;
	private int retryCounter=0;
	private DiameterSession serverStackSession;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterShMessageFactory diameterShMsgFactory;
	private DiameterStack stack=null;

	public ShPushNotificationRequestImpl(ShSession ShSession, int type,String remoteRealm){
		super(type);
		stack=((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack();
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
		logger.debug("Inside ShPushNotificationRequestImpl constructor ");
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
	public byte[] getRawUserData() throws ShResourceException {
		return new byte[0];
	}

	@Override
	public String getUserData() throws ShResourceException {
		return null;
	}

}
