package com.baypackets.ase.ra.diameter.sh.stackif;


import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.ra.diameter.sh.ShUserDataResponse;
import com.baypackets.ase.ra.diameter.sh.impl.ShMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.ra.diameter.sh.utils.AvpCodes;
import com.baypackets.ase.ra.diameter.sh.utils.ResultCodes;
import com.baypackets.ase.ra.diameter.sh.utils.ShStackConfig;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShUserDataRequestImpl extends ShAbstractRequest implements ShUserDataRequest {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ShUserDataRequestImpl.class);

	private ShSession m_shSession;
	private int retryCounter=0;
	private DiameterSession serverStackSession;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterShMessageFactory diameterShMsgFactory;
	private DiameterStack stack=null;
	private String senderOriginHoststatelless;

	public void setStatelessSenderOriginHost(String nextHop) {
		// TODO Auto-generated method stub
		senderOriginHoststatelless = nextHop;
	}

	public String getStatelessSenderOriginHost() {
		// TODO Auto-generated method stub
		return senderOriginHoststatelless;
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


	public ShUserDataRequestImpl(int type){
		super(type);

		stack=((ShStackServerInterfaceImpl) ShResourceAdaptorImpl.stackInterface).getDiameterStack();
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();

		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();


	}

	public ShUserDataRequestImpl(ShSession shSession, int type, String remoteRealm,String msisdn){
		super(shSession);
		logger.debug("Inside ShUserDataRequestImpl(ShSession) constructor with realm -->" + remoteRealm);
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterShMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterShMsgFactory();
		try {
			this.stackObj = ShMessageFactoryImpl.createUDR(remoteRealm,msisdn);
		} catch (DiameterException e) {
			logger.debug("Exception in creating UDR",e);
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

	public ShSession getM_shSession() {
		return m_shSession;
	}

	public void setM_shSession(ShSession m_shSession) {
		this.m_shSession = m_shSession;
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
	public void toXML(StringBuilder builder) {	}

	@Override
	public ShUserDataResponse createAnswer(String returnCode) throws ShResourceException {

		if(logger.isDebugEnabled()){
			logger.debug("Inside createAnswer(long) findReturnCodeName From stack for code  object--> " +this.getStackObj());
		}

		DiameterMessage answer=null;
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Create  UDA --> from "+ returnCode);
			}
           
			/* Create a User Data Answer with the following mandatory parameters
    		Result-Code       :  DIAMETER_SUCCESS
    		User-Data         :  UserData Buffer*/
			String UserData ="<?xml version='1.0' encoding='UTF-8'?><Sh-Data><Extension><Extension>" +
					"<Extension><Extension><EPSLocationInformation><E-UTRANCellGlobalId>AwIWCG1nFg==</E-UTRANCellGlobalId>" +
					"<TrackingAreaId>AwIW4pM=</TrackingAreaId><MMEName>04.mmecda.mmegidac0.mme.epc.mnc610.mcc302.3gppnetwork.org</MMEName>" +
					"<AgeOfLocationInformation>0</AgeOfLocationInformation><Extension><VisitedPLMNID>302610</VisitedPLMNID>" +
					"</Extension></EPSLocationInformation></Extension></Extension></Extension></Extension></Sh-Data>";

			answer = diameterShMsgFactory.createUserDataAnswer(returnCode,UserData);

			/* Send the message through the session */
			logger.info("Created  UDA : " + answer.toString());

			if(ShStackConfig.isStateless()) {
				// Set the End-To-End identifier in answer as received in the
				// request

				if(logger.isDebugEnabled()){
					logger.debug("set stateless attributes on Answer->");
				}

				if(logger.isDebugEnabled()){
					logger.debug("set getEndToEndIdentifier->" +getEndToEndIdentifier());
				}
				answer.setEndtoEndId(getEndToEndIdentifier());
				// Set the Hop-by-Hop identifier in answer as received in the
				// request
				if(logger.isDebugEnabled()){
					logger.debug("set getHopByHopIdentifier->" +getHopByHopIdentifier());
				}
				answer.setHopbyHopId(getHopByHopIdentifier());
				// Set Proxy-Info AVPs if present in request.

				if(logger.isDebugEnabled()){
					logger.debug("add proxyinfo AVPS--> "+ this.getStackObj().getProxyInfoAVPs());
				}

				answer.addAVPs(this.getStackObj().getProxyInfoAVPs());
				// Set the Session-Id AVP in answer as received in the request
				answer.setSessionIdAVPValue(this.getStackObj()
						.getSessionIdAVPValue());
				String nextHop = null;
				// Get Route-Record AVPs if present in request
				ArrayList<DiameterAVP> routeRecordList = this.getStackObj()
						.getRouteRecordAVPs();
				if (routeRecordList != null) {
					if(logger.isDebugEnabled()){
						logger.debug("add RouteRecord AVPS--> "+ routeRecordList);
					}
					// Add Route-Record AVPs to answer
					answer.addAVPs(routeRecordList);
				}
			}
			if(logger.isDebugEnabled()){
				logger.debug("Created  diamter Answer--> "+ answer);
			}
		} catch (DiameterException e) {
			logger.error(" diameter xception "+ e);
		}
		ShUserDataResponseImpl response=new ShUserDataResponseImpl(answer);
		response.setResultCode(returnCode);
		response.setProtocolSession(this.getProtocolSession());
		response.setRequest(this);
		return response;
	}

	@Override
	public ShUserDataResponse createAnswer(long vendorId, int experimentalResultCode) throws ShResourceException {
	if(logger.isDebugEnabled()){
			logger.debug("Inside createAnswer(long vendorId, long experimentalResultCode)");
		}

		DiameterMessage answer=null;
		try {

			/* Create a User Data Answer with the following mandatory parameters
    		Result-Code       :  DIAMETER_SUCCESS
    		User-Data         :  UserData Buffer*/
			String UserData =
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?> "      +
							"<IMSSubscription><PrivateID>xav.l</PrivateID> "   +
							"<ServiceProfile><PublicIdentity>"                 +
							"<BarringIndication>0</BarringIndication> "        +
							"<Identity>sip:xavierl@ims.acme.org</Identity> "   +
							"</PublicIdentity></ServiceProfile> "              +
							"</IMSSubscription> ";


			answer = diameterShMsgFactory.createUserDataAnswer
							(ResultCodes.getReturnCode((int)experimentalResultCode),UserData);

			/* Send the message through the session */
			logger.info("Sending UDA : " + answer.toString());
			//answer.setResultCodeAVPValue(ResultCodeAvp.getName((int)l));
			ArrayList<DiameterAVP> list=new ArrayList<DiameterAVP>();



			DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
					"Vendor-Id", "3GPP", (int)vendorId);

			list.add(avpIn);
			answer.addAVPs(list);

			if(ShStackConfig.isStateless()) {
				// Set the End-To-End identifier in answer as received in the
				// request

				logger.info("set stateless attributes on Answer->");
				answer.setEndtoEndId(getEndToEndIdentifier());
				// Set the Hop-by-Hop identifier in answer as received in the
				// request
				answer.setHopbyHopId(getHopByHopIdentifier());
				// Set Proxy-Info AVPs if present in request.
				answer.addAVPs(this.getStackObj().getProxyInfoAVPs());
				// Set the Session-Id AVP in answer as received in the request
				answer.setSessionIdAVPValue(this.getStackObj()
						.getSessionIdAVPValue());
				String nextHop = null;
				// Get Route-Record AVPs if present in request
				ArrayList<DiameterAVP> routeRecordList = this.getStackObj()
						.getRouteRecordAVPs();
				if (routeRecordList != null) {
					// Add Route-Record AVPs to answer
					answer.addAVPs(routeRecordList);
				}
			}
		} catch (DiameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ShUserDataResponseImpl response=new ShUserDataResponseImpl(answer);
		response.setRequest(this);
		return response;

	}



	@Override
	public long getAuthApplicationId() throws ShResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("getAuthApplicationId Return 4");
		}
		return 4;
	}

	@Override
	public ArrayList<DiameterAVP> getRouteRecords() throws ShResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRouteRecords()");
		}
		return stackObj.getRouteRecordAVPs();
	}

	@Override
	public String getServiceContextId() throws ShResourceException {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside getServiceContextId()");
		}

		String svcContextId = null;
		ArrayList<DiameterAVP> svcContextList = stackObj
				.getAVP(AvpCodes.Service_Context_Id);

		if (svcContextList != null) {
			DiameterAVP avp = svcContextList.get(0);

			if (logger.isDebugEnabled()) {
				logger.debug("Inside getServiceContextId() avp is " + avp);
			}
			if (avp instanceof DiameterOctetStringAVP) {
				DiameterOctetStringAVP ctxId = (DiameterOctetStringAVP) avp;
				svcContextId = ctxId.getStringValue();
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Inside getServiceContextId() not found");
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Inside getServiceContextId() returning  " + svcContextId);
		}
		return svcContextId;
	}


	@Override
	public ValidationRecord validate() {
		return null;
	}

	@Override
	public void addServiceContextId(String value)
			throws ShResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside addServiceContextId() "+value);
		}
		DiameterOctetStringAVP avpIn=diameterMsgFactory.createOctetStringAVP(name,"base",value);

		getStackObject().add(avpIn);
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
			logger.debug("Inside addDiameterInteger32AVP() name "+name +" VenodrName "+name +" Value "+value);
		}
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
				name, vendorName, value);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterInteger32AVP created AVP" +avpIn);
		}
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
			logger.debug("Inside addDiameterInteger64AVP() name "+name +" VenodrName "+name +" Value "+value);
		}
		DiameterInteger64AVP avpIn=diameterMsgFactory.createInteger64AVP(
				name, vendorName, (int)value);
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterInetger64AVP created AVP" +avpIn);
		}
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
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterUnsigned32AVP() name "+name +" VenodrName "+name +" Value "+value);
		}
		DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
				name, vendorName, value);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterUnsigned32AVP created AVP" +avpIn);
		}

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
			logger.debug("Inside addDiameterUnsigned64AVP() name "+name +" VenodrName "+name +" Value "+value);
		}
		DiameterUnsigned64AVP avpIn=diameterMsgFactory.createUnsigned64AVP(
				name,vendorName, value);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterUnsigned64AVP created AVP" +avpIn);
		}
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
			logger.debug("Inside addDiameterOctetStringAVP(byte[]) name "+name +" VenodrName "+name +" Value "+value);
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
			logger.debug("Inside addDiameterOctetStringAVP(strin) name "+name +" VenodrName "+name +" Value "+value);
		}
		DiameterOctetStringAVP avpIn=diameterMsgFactory.createOctetStringAVP(name,vendorName,value);
		getStackObject().add(avpIn);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterOctetStringAVP created AVP" +avpIn);
		}
	}


	/**
	 * This method is used to add grouped avp  
	 * @param name
	 * @param vendorName
	 * @param value
	 * @return
	 */
	public void  addDiameterGroupedAVP(String avpName,String vendorName,List<DiameterAVP> groupAvps){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP avpName "+avpName + "VendorName " +vendorName +" groupedAvps "+groupAvps);
		}
		DiameterGroupedAVP avpIn=diameterMsgFactory.createGroupedAVP(avpName, vendorName);

		if(groupAvps!=null){
			avpIn.setValue(groupAvps);
		}
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
	public AvpDiameterGrouped  addDiameterGroupedAVP(String avpName,String vendorName){
		AvpDiameterGrouped avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP avpName "+avpName + "VendorName " +vendorName);
		}
		DiameterGroupedAVP avpIn=diameterMsgFactory.createGroupedAVP(avpName, vendorName);

		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP created AVP" +avp);
		}
		avp= new AvpDiameterGrouped(avpIn, diameterMsgFactory);
		getStackObject().add(avpIn);
		return avp;
	}

	@Override
	public void addDiameterAVPs(ArrayList<DiameterAVP> avpList){
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDiameterGroupedAVP  "+avpList);
		}
		getStackObject().addAVPs(avpList);
	}

	@Override
	public DiameterShMessageFactory getDiameterShMessageFactory() {
		return this.diameterShMsgFactory;
	}

	@Override
	public void setDestinationHost(String host){
		stackObj.setDestinationHostAVPValue(host);
	}
	
	@Override
	public void setServiceContextId(String contextId){
		addDiameterOctetStringAVP("Service-Context-Id", "base",contextId);
	}

	public void  setSessionIDAVP(String sessionID){
		if(logger.isDebugEnabled()){
			logger.debug("setSessionIDAVP "+ sessionID);
		}

		getStackObject().setSessionIdAVPValue(sessionID);
	}

}
