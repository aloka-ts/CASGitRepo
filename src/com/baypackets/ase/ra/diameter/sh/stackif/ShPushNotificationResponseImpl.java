package com.baypackets.ase.ra.diameter.sh.stackif;


import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.sh.ShPushNotificationResponse;
import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.utils.ResultCodes;
import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.resource.Request;

import java.util.ArrayList;

public class ShPushNotificationResponseImpl extends ShAbstractResponse implements ShPushNotificationResponse {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ShPushNotificationResponseImpl.class);

	/**
	 *	This attribute contains the stack object which is to be used
	 *	for all the set/get method
	 *
	 */
	private ShSession m_ShSession;
	private int retryCounter=0;
	private ShUserDataRequest shRequest;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterShMessageFactory diameterShMsgFactory;

	public ShPushNotificationResponseImpl(int answer, ShUserDataRequestImpl containerReq){
		super(answer);
		if(logger.isDebugEnabled()){
			logger.debug("Inside ShPushNotificationResponseImpl constructor answer " +answer +" from Request " +containerReq);
		}

		diameterMsgFactory= ((ShStackServerInterfaceImpl) ShResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack().getDiameterMessageFactory();

		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterClientIfoMsgFactory();//.getDiameterShMessageFactory(DiameterStack.RELEASE13);

		shRequest=containerReq;
		try {
			super.stackObj=diameterShMsgFactory.createUserDataAnswer(ResultCodes.getReturnCode(answer));
		} catch (DiameterInvalidArgumentException e) {
			logger.error(" Exception "+e);
		} catch (DiameterException e) {
			logger.error(" Exception "+e);
		}

	}

	public ShPushNotificationResponseImpl(ShSession ShSession){
		super(ShSession);
		//stackObj = new MessageProfileUpdateAnswer();
		this.m_ShSession=ShSession;
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack().getDiameterMessageFactory();

		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterClientIfoMsgFactory();//.getDiameterShMessageFactory(DiameterStack.RELEASE13);
	}

	public ShPushNotificationResponseImpl(DiameterMessage stkObj){
		super(stkObj);

		super.stackObj=stkObj;

		if(logger.isDebugEnabled()){
			logger.debug("Inside ShPushNotificationResponseImpl constructor stackObj " +stkObj);
		}
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
	}

	private String resultCode=null;

	public void setResultCode(String resultcode){
		resultCode=resultcode;

		if(logger.isDebugEnabled()){
			logger.debug("setResultCode stackObj " +ResultCodes.getReturnCode(resultcode,false));
		}

		stackObj.setResultCodeAVPValue(resultcode);//(ResultCodes.getReturnCode(resultcode));

		if(logger.isDebugEnabled()){
			logger.debug("setResultCode return");
		}
	}
	public void incrementRetryCounter() {
		this.retryCounter++;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public void setStackObj(DiameterMessage stkObj){
		super.setStackObject(stkObj);
	}

	public DiameterMessage getStackObj(){
		return stackObj;
	}


	@Override
	public Request getRequest() {
		return null;
	}

	@Override
	public byte getVersion() {
		return 0;
	}

	@Override
	public boolean isPerformFailover() {
		return false;
	}

	@Override
	public void setPerformFailover(boolean performFailover) {

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
	public String getResultCode(boolean experimental) throws ShResourceException {
		return null;
	}

	@Override
	public String getUserData() {
		return null;
	}

	@Override
	public void setUserData(String uda) {

	}

	////////////////////////////////////////////////////////////////////
	////////////// ShUserDataResponse Specific API ENDS  ///////////////
	////////////////////////////////////////////////////////////////////

}
