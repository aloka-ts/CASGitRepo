package com.baypackets.ase.ra.diameter.sh.stackif;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned32;
import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.ra.diameter.sh.ShUserDataResponse;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.ra.diameter.sh.utils.ResultCodes;
import com.baypackets.ase.resource.Request;

import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShUserDataResponseImpl extends ShAbstractResponse implements ShUserDataResponse {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ShUserDataResponseImpl.class);

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private String userData;
	private ShSession m_ShSession;
	private int retryCounter=0;
	private ShUserDataRequest shRequest;
	
	private static final long EXPERIMENTAL_RESULT=297;
	private static final long EXPERIMENTAL_RESULT_CODE=298;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterShMessageFactory diameterShMsgFactory;

	public ShUserDataResponseImpl(int answer, ShUserDataRequestImpl containerReq){
		super(answer);
		if(logger.isDebugEnabled()){
			logger.debug("Inside ShUserDataResponseImpl constructor answer " +answer +" from Request " +containerReq);
		}

		diameterMsgFactory= ((ShStackServerInterfaceImpl) ShResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterClientIfoMsgFactory();//.getDiameterShMessageFactory(DiameterStack.RELEASE13);
		shRequest=containerReq;
		try {
		/* Create a User Data Answer with the following mandatory parameters
               Result-Code       :  DIAMETER_SUCCESS
               User-Data         :  UserData Buffer    */
			String UserData =
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?> "      +
							"<IMSSubscription><PrivateID>xav.l</PrivateID> "   +
							"<ServiceProfile><PublicIdentity>"                 +
							"<BarringIndication>0</BarringIndication> "        +
							"<Identity>sip:xavierl@ims.acme.org</Identity> "   +
							"</PublicIdentity></ServiceProfile> "              +
							"</IMSSubscription> ";
			super.stackObj =
					diameterShMsgFactory.createUserDataAnswer("DIAMETER_SUCCESS",UserData);

		} catch (DiameterInvalidArgumentException e) {
			logger.error(" Exception "+e);
		} catch (DiameterException e) {
			logger.error(" Exception "+e);
		}

	}

	public ShUserDataResponseImpl(ShSession ShSession){
		super(ShSession);
		this.m_ShSession=ShSession;
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterClientIfoMsgFactory();//.getDiameterShMessageFactory(DiameterStack.RELEASE13);
	}

	public ShUserDataResponseImpl(DiameterMessage stkObj){
		super(stkObj);
		super.stackObj=stkObj;
		if(logger.isDebugEnabled()){
			logger.debug("Inside ShUserDataResponseImpl constructor stackObj " +stkObj);
		}
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
	}

	private String resultCode=null;

	public void setResultCode(String resultcode){
		resultCode=resultcode;

		if(logger.isDebugEnabled()){
			logger.debug("setResultCode stackObj " + ResultCodes.getReturnCode(resultcode,false));
		}

		stackObj.setResultCodeAVPValue(resultcode);

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

	////////////////////////////////////////////////////////////////
	//// SH UDA method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////
	public void  addDiameterGroupedAVP(String avpName,String vendorName,List<DiameterAVP> groupAvps){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP avpName "+avpName + "VendorName " +vendorName +" groupedAvps "+groupAvps);
		}
		DiameterGroupedAVP avpIn=diameterMsgFactory.createGroupedAVP(avpName, vendorName);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP created AVP" +avpIn);
		}

		if(groupAvps!=null){
			avpIn.setValue(groupAvps);
		}
		getStackObject().add(avpIn);
	}

	@Override
	public void setDestinationHost(String host){
		stackObj.setDestinationHostAVPValue(host);
	}


	@Override
	public void setServiceContextId(String contextId){
		addDiameterOctetStringAVP("Service-Context-Id", "base",contextId);
	}

	/**
	 *  Retrieving a single DiameterIdentity value from OriginHost AVPs.
	 */
	public java.lang.String getOriginHost() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getOriginHost()");
		}
		return stackObj.getOriginHostAVPValue();
	}
//
	/**
	 *  Retrieving a single DiameterIdentity value from OriginRealm AVPs.
	 */
	public java.lang.String getOriginRealm() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getOriginRealm()");
		}
		return stackObj.getOriginRealmAVPValue();
	}

	@Override
	public String getUserData() {
		return userData;
	}

	@Override
	public void setUserData(String uda) {
		userData=uda;
	}

	/**
		 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
		 */
		@Override
		public void addAuthApplicationId(long value) throws ShResourceException {

			if(logger.isDebugEnabled()){
				logger.debug("Inside addAuthApplicationId  "+value);
			}
			DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
					"Auth-Application-Id", "base", value);
			getStackObject().add(avpIn);

		}


	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ArrayList<DiameterAVP> getGroupedProxyInfos() throws ShResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getGroupedProxyInfos()");
		}
		ArrayList<DiameterAVP> stackAv= stackObj.getProxyInfoAVPs();//getGroupedProxyInfos();

		return stackAv;
	}

	/**
	 *  Retrieving a single Unsigned32 value from ResultCode AVPs.
	 * @return
	 */
	public String getResultCode(boolean experimental) throws ShResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getResultCode()-->");
		}
		if(logger.isDebugEnabled()){
			logger.debug("Inside getResultCode()-->Result code AVP is " +stackObj.getResultCodeAVPValue());
		}
		
		if (experimental) {
			List<DiameterAVP> avps = super.getAvp(EXPERIMENTAL_RESULT); // Experimental-Result

			if (!avps.isEmpty() && avps.get(0) instanceof DiameterGroupedAVP) {

				DiameterGroupedAVP expResultCGrpedAvp = (DiameterGroupedAVP) avps
						.get(0);

				if (logger.isDebugEnabled()) {
					logger.debug("Inside getResultCode()-->got experimental result avp return AVP"
							+ expResultCGrpedAvp);
				}
				List<DiameterAVP> expResultCGrpedAvps = expResultCGrpedAvp
						.getValue();

				for (DiameterAVP avp : expResultCGrpedAvps) {

					if (avp != null
							&& avp.getCode() == EXPERIMENTAL_RESULT_CODE) {

						DiameterUnsigned32AVP expResult = (DiameterUnsigned32AVP) avp;
						if (logger.isDebugEnabled()) {
							logger.debug("Inside getResultCode()-->Exp result"
									+ ((int) expResult.getValue()));
						}
						String resultDesc = ResultCodes.getExperimentalResultCode((int) expResult
										.getValue());

						if (logger.isDebugEnabled()) {
							logger.debug("Inside getResultCode()-->Exp result"
									+ resultDesc);
						}

						return resultDesc;

					}
				}
			}
			return null;
		}
		return stackObj.getResultCodeAVPValue();
	}

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 * @return
	 */
	public ArrayList<DiameterAVP> getRouteRecords() throws ShResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRouteRecords()");
		}
		return stackObj.getRouteRecordAVPs();
	}

	@Override
	/**
	 * This method is used to add 32 bit integer AVP
	 * @param name
	 * @param value
	 * @param vendorName
	 * @return
	 */
	public void  addDiameterInteger32AVP(String name, int value,String vendorName){

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterInetegr32AVP()");
		}
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
				name, vendorName, value);
		// avp= new AvpDiameterInteger32(avpIn);
		getStackObject().add(avpIn);
	}

	@Override
	/**
	 * This method is used to add 64 integer AVP
	 * @param name
	 * @param value
	 * @param vendorName
	 * @return
	 */
	public void  addDiameterInteger64AVP(String name, long value,String vendorName){

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterInetegr64AVP()");
		}
		DiameterInteger64AVP avpIn=diameterMsgFactory.createInteger64AVP(
				name, vendorName, value);
		getStackObject().add(avpIn);

	}

	@Override
	/**
	 * This method is used to add Unsigned 32 AVP
	 * @param name
	 * @param value
	 * @param vendorName
	 * @return
	 */
	public void  addDiameterUnsigned32AVP(String name, long value,String vendorName){
		AvpDiameterUnsigned32 avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterUnsigned32AVP()");
		}
		DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
				name, vendorName, value);
		getStackObject().add(avpIn);

	}

	@Override
	/**
	 * This method is used to unsigned 64 AVP
	 * @param name
	 * @param value
	 * @param vendorName
	 * @return
	 */
	public void  addDiameterUnsigned64AVP(String name, BigInteger value,String vendorName){

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterUnsigned64AVP()");
		}
		DiameterUnsigned64AVP avpIn=diameterMsgFactory.createUnsigned64AVP(
				name,vendorName, value);
		getStackObject().add(avpIn);
	}

	@Override
	/**
	 * This method is used to add Float 32 bit
	 * @param name
	 * @param vendorName
	 * @param value
	 * @return
	 */
	public void  addDiameterFloat32AVP(String name,String vendorName, float value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterFloat32AVP()");
		}
		DiameterFloat32AVP avpIn=diameterMsgFactory.createFloat32AVP(
				name,vendorName, value);
		getStackObject().add(avpIn);

	}

	@Override
	/**
	 * This method is used to add 64 bit float
	 * @param name
	 * @param vendorName
	 * @param value
	 * @return
	 */
	public void  addDiameterFloat64AVP(String name, String vendorName,double value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterFloat64AVP()");
		}
		DiameterFloat64AVP avpIn=diameterMsgFactory.createFloat64AVP(
				name, vendorName, value);
		getStackObject().add(avpIn);
	}

	@Override
	/**
	 * This method is used to add generic AVP
	 * @param avpCode
	 * @param vendorId
	 * @param value
	 * @return
	 */
	public void  addDiameterGenericAVP(long avpCode,long vendorId, byte[] value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGenericAVP()");
		}
		DiameterGenericAVP avpIn=diameterMsgFactory.createGenericAVP(avpCode, vendorId, value);
		getStackObject().add(avpIn);

	}

	@Override
	/**
	 * This method is used to add octet string avp byte [] 
	 * @param name
	 * @param vendorName
	 * @param value
	 * @return
	 */
	public void  addDiameterOctetStringAVP(String name,String vendorName,byte[] value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterOctetStringAVP(byte[]) name "+name +" value "+ value +" Vendor "+vendorName);
		}
		DiameterOctetStringAVP avpIn=diameterMsgFactory.createOctetStringAVP(name,vendorName,value);
		getStackObject().add(avpIn);
	}

	@Override
	/**
	 * This method is used to add OctetString AVP as string value
	 * @param name
	 * @param vendorName
	 * @param value
	 * @return
	 */
	public void  addDiameterOctetStringAVP(String name,String vendorName,String value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterOctetStringAVP(string) name "+name +" value "+ value +" Vendor "+vendorName);
		}
		DiameterOctetStringAVP avpIn=diameterMsgFactory.createOctetStringAVP(name,vendorName,value);
		getStackObject().add(avpIn);
	}


	@Override
	/**
	 * This method is used to add grouped avp  
	 * @param name
	 * @param vendorName
	 * @param value
	 * @return
	 */
	public AvpDiameterGrouped  addDiameterGroupedAVP(String avpName,String vendorName ){
		AvpDiameterGrouped avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP avpName "+avpName + " VendorName " +vendorName);
		}
		DiameterGroupedAVP avpIn=diameterMsgFactory.createGroupedAVP(avpName, vendorName);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP avpName grouped avp created is "+ avpIn);
		}

		getStackObject().add(avpIn);
		return avp;
	}



	/**
	 * Ths method s sued to add generc type avps
	 */
	@Override
	public void addDiameterAVPs(ArrayList<DiameterAVP> groupedAvps){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP  "+groupedAvps);
		}

		getStackObject().addAVPs(groupedAvps);
	}

	@Override
	public DiameterShMessageFactory getDiameterShMessageFactory() {
		return this.diameterShMsgFactory;
	}

	public void setRequest(ShUserDataRequest request)
	{
		if(logger.isDebugEnabled()){
			logger.debug("Inside setRequest()");
		}
		this.shRequest=request;
	}
	@Override
	public Request getRequest() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRequest()");
		}
		return this.shRequest;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ShUserDataResponse[stackObj=" +stackObj + "]";
	}

	@Override
	public boolean isPerformFailover() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPerformFailover(boolean performFailover) {
		// TODO Auto-generated method stub

	}
	@Override
	public byte getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getByteArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMessageLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public StandardEnum getStandard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toXML(StringBuilder builder) {
		// TODO Auto-generated method stub

	}

	@Override
	public ValidationRecord validate() {
		// TODO Auto-generated method stub
		return null;
	}
}
