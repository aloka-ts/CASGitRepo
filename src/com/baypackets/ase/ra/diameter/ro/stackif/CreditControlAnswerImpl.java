package com.baypackets.ase.ra.diameter.ro.stackif;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterFloat32;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterFloat64;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGeneric;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterInteger32;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterInteger64;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterOctetString;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned32;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned64;
import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.ro.CreditControlAnswer;
import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.ro.impl.RoSession;
import com.baypackets.ase.ra.diameter.ro.utils.AvpCodes;
import com.baypackets.ase.ra.diameter.ro.utils.ResultCodes;
import com.baypackets.ase.resource.Request;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterException;
import fr.marben.diameter.DiameterFloat32AVP;
import fr.marben.diameter.DiameterFloat64AVP;
import fr.marben.diameter.DiameterGenericAVP;
import fr.marben.diameter.DiameterGroupedAVP;
import fr.marben.diameter.DiameterInteger32AVP;
import fr.marben.diameter.DiameterInteger64AVP;
import fr.marben.diameter.DiameterInvalidArgumentException;
import fr.marben.diameter.DiameterMessage;
import fr.marben.diameter.DiameterMessageFactory;
import fr.marben.diameter.DiameterOctetStringAVP;
import fr.marben.diameter.DiameterStack;
import fr.marben.diameter.DiameterUnsigned32AVP;
import fr.marben.diameter.DiameterUnsigned64AVP;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public class CreditControlAnswerImpl extends RoAbstractResponse implements CreditControlAnswer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(CreditControlAnswerImpl.class);

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	//private DiameterMessage stackObj;
	private RoSession m_roSession;
	private int retryCounter=0;
    private CreditControlRequest roRequest;

	private DiameterMessageFactory diameterMsgFactory;
	private DiameterRoMessageFactory diameterRoMsgFactory;
	
	public CreditControlAnswerImpl(int answer, CreditControlRequestImpl containerReq){
		super(answer);
		if(logger.isDebugEnabled()){
			logger.debug("Inside CreditControlAnswerImpl constructor answer " +answer +" from Request " +containerReq);
		}
		
		diameterMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack().getDiameterMessageFactory();
		
		diameterRoMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterClientIfoMsgFactory();//.getDiameterRoMessageFactory(DiameterStack.RELEASE13);
		
		roRequest=containerReq;
		try {
			super.stackObj=diameterRoMsgFactory.createCreditControlAnswer(ResultCodes.getReturnCode(answer), roRequest.getEnumCCRequestType().name(), roRequest.getCCRequestNumber());
		} catch (DiameterInvalidArgumentException e) {
			logger.error(" Exception "+e);
		} catch (RoResourceException e) {
			logger.error(" Exception "+e);
		} catch (DiameterException e) {
			logger.error(" Exception "+e);
		}

	}

	public CreditControlAnswerImpl(RoSession roSession){
		super(roSession);
		//stackObj = new MessageProfileUpdateAnswer();
		this.m_roSession=roSession;
        diameterMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack().getDiameterMessageFactory();
		
		diameterRoMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterClientIfoMsgFactory();//.getDiameterRoMessageFactory(DiameterStack.RELEASE13);
	}

	public CreditControlAnswerImpl(DiameterMessage stkObj){
		super(stkObj);
		
		super.stackObj=stkObj;
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside CreditControlAnswerImpl constructor stackObj " +stkObj);
		}
		diameterMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		diameterRoMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterRoMsgFactory();//Stack().getDiameterRoMessageFactory(DiameterStack.RELEASE13);
	}

	private String resultCode=null;

	public void setResultCode(String resultcode){
		resultCode=resultcode;
		
		if(logger.isDebugEnabled()){
			logger.debug("setResultCode stackObj " +ResultCodes.getReturnCode(resultcode));
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

	////////////////////////////////////////////////////////////////
	//// RO CCA method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////


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
			
			avp= new AvpDiameterGrouped(avpIn,diameterMsgFactory);
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
	
	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
	@Override
	public void addAuthApplicationId(long value) throws RoResourceException {
			
		if(logger.isDebugEnabled()){
			logger.debug("Inside addAuthApplicationId  "+value);
		}
			DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
					"Auth-Application-Id", "base", value);
			 getStackObject().add(avpIn);

	}

	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
	public void addCCRequestNumber(long value) throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCRequestNumber() " + value);
		}
		DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
				"CC-Request-Number", "base", value);
		 getStackObject().add(avpIn);
	}

	@Override
	/**
	 *  Adding CCRequestType AVP of type Enumerated to the message.
	 */
	public void addCCRequestType(int  type,String vendorName) throws RoResourceException {
		
		AvpDiameterUnsigned32 avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCRequestType()");
		}
		DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
				"CC-Request-Type", "base", (int)type);
		 getStackObject().add(avpIn);
	}

	@Override
	/**
	 *  Adding CCSessionFailover AVP of type Enumerated to the message.
	 */
	public void addCCSessionFailover(int value) throws RoResourceException {
		
		AvpDiameterUnsigned32 avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCSessionFailover()");
		}
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
				"CC-Session-Failover", "base", value);
		 getStackObject().add(avpIn);

	}

	@Override
	/**
	 *  Adding CreditControlFailureHandling AVP of type Enumerated to the message.
	 */
	public void addCreditControlFailureHandling(String value) throws RoResourceException {
		
		AvpDiameterUnsigned32 avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCreditControlFailureHandling() " +value);
		}
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
				"Credit-Control-Failure-Handling", "base", Integer.parseInt(value));

		getStackObject().add(avpIn);

	}

	@Override
	/**
	 *  Adding DirectDebitingFailureHandling AVP of type Enumerated to the message.
	 */
	public void addDirectDebitingFailureHandling(String value) throws RoResourceException {
		AvpDiameterUnsigned32 avp=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside addDirectDebitingFailureHandling() " +value);
		}
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
				"Direct-Debiting-Failure-Handling", "base", Integer.parseInt(value));
		getStackObject().add(avpIn);

	}

	/**
	 *  Adding CostInformation AVP of type Grouped to the message.
	 */
	public List<DiameterAVP> addCostInformation(List<DiameterAVP> unitValue,long currencyCode ,String currencyUnit) throws RoResourceException {
		List<DiameterAVP> avp=null;
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCRequestNumber()");
			}
			avp=diameterRoMsgFactory.createCostInformationAVP(unitValue, currencyCode, currencyUnit);
			DiameterGroupedAVP avpIn=diameterMsgFactory.createGroupedAVP("Cost-Information", "base");
			avpIn.setValue(avp);
			getStackObject().add(avpIn);
		} catch (DiameterInvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DiameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return avp;
	}
	
	@Override
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
			
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDiameterGroupedAVP created AVP" +avpIn);
			}
			
			if(groupAvps!=null){
			   avpIn.setValue(groupAvps);
			}
			getStackObject().add(avpIn);
	     }
	
//	/**
//	 *  Adding FailedAVP AVP of type Grouped to the message.
//	 */
//	public FailedAVPAvp addGroupedFailedAVP() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedFailedAVP()");
//			}
//			return new FailedAVPAvp(stackObj.addGroupedFailedAVP());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedFailedAVP",e);
//		}
//	}
//
//	/**
//	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
//	 */
//	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedMultipleServicesCreditControl()");
//			}
//			return new MultipleServicesCreditControlAvp(stackObj.addGroupedMultipleServicesCreditControl());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedMultipleServicesCreditControl",e);
//		}
//	}

//	/**
//	 *  Adding ProxyInfo AVP of type Grouped to the message.
//	 */
//	public ProxyInfoAvp addGroupedProxyInfo() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedProxyInfo()");
//			}
//			return new ProxyInfoAvp(stackObj.addGroupedProxyInfo());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedProxyInfo",e);
//		}
//	}
//
//	/**
//	 *  Adding RemainingBalance AVP of type Grouped to the message.
//	 */
//	public RemainingBalanceAvp addGroupedRemainingBalance() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedRemainingBalance()");
//			}
//			return new RemainingBalanceAvp(stackObj.addGroupedRemainingBalance());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedRemainingBalance",e);
//		}
//	}

//	/**
//	 *  Adding ServiceInformation AVP of type Grouped to the message.
//	 */
//	public ServiceInformationAvp addGroupedServiceInformation() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedServiceInformation()");
//			}
//			return new ServiceInformationAvp(stackObj.addGroupedServiceInformation());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedServiceInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding LowBalanceIndication AVP of type Enumerated to the message.
//	 */
//	public LowBalanceIndicationAvp addLowBalanceIndication(LowBalanceIndicationEnum value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addLowBalanceIndication()");
//			}
//			return new LowBalanceIndicationAvp(stackObj.addLowBalanceIndication(LowBalanceIndicationEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addLowBalanceIndication",e);
//		}
//	}

	//TODO
	//	/**
	//	 *  Adding RedirectHost AVP of type DiameterURI to the message.
	//	 */
	//	public RedirectHostAvp addRedirectHost(URI value) throws RoResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside addRedirectHost()");
	//			}
	//			return new RedirectHostAvp(stackObj.addRedirectHost(value));
	//		} catch (ValidationException e) {
	//			throw new RoResourceException("Exception in addRedirectHost",e);
	//		}
	//	}

	/**
	 *  Adding RedirectHostUsage AVP of type Enumerated to the message.
	 */
//	public RedirectHostUsageAvp addRedirectHostUsage(RedirectHostUsageEnum value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addRedirectHostUsage()");
//			}
//			return new RedirectHostUsageAvp(stackObj.addRedirectHostUsage(RedirectHostUsageEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addRedirectHostUsage",e);
//		}
//	}

//	/**
//	 *  Adding RedirectMaxCacheTime AVP of type Unsigned32 to the message.
//	 */
//	public RedirectMaxCacheTimeAvp addRedirectMaxCacheTime(long value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addRedirectMaxCacheTime()");
//			}
//			return new RedirectMaxCacheTimeAvp(stackObj.addRedirectMaxCacheTime(value));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addRedirectMaxCacheTime",e);
//		}
//	}
//
//	/**
//	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
//	 */
//	public RouteRecordAvp addRouteRecord(java.lang.String value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addRouteRecord()");
//			}
//			return new RouteRecordAvp(stackObj.addRouteRecord(value));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addRouteRecord",e);
//		}
//	}

	

//	/**
//	 *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
//	 */
//	public long getAuthApplicationId() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getAuthApplicationId()");
//			}
//			return stackObj.getAuthApplicationId();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getAuthApplicationId",e);
//		}
//	}

	/**
	 *  Retrieving a single Unsigned32 value from CCRequestNumber AVPs.
	 */
	public long getCCRequestNumber() throws RoResourceException {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCRequestNumber()");
		}
		ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Request_Number);
	
		DiameterAVP avp=ccrenumberList.get(0);
		
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCRequestNumber()  "+avp);
		}
		
		long ccnum=-1;
		if (avp.getInstanceType().equals("Integer32")) {
			DiameterInteger32AVP ccNum = (DiameterInteger32AVP) avp;
			ccnum= ccNum.getValue();
		} else if (avp.getInstanceType().equals("Unsigned32")) {
			DiameterUnsigned32AVP ccNum = (DiameterUnsigned32AVP) avp;
			ccnum= ccNum.getValue();
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCRequestNumber() return "+ccnum);
		}
		return ccnum;
	}

	/**
	 *  Retrieving a single Enumerated value from CCRequestType AVPs.
	 */
	public long getCCRequestType() throws RoResourceException {
		//AvpDiameterUnsigned32 ccnumber=null;
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCRequestType() from stackobj");
		}
		
		ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Request_Type);
		
		
		
//		stack.EnumCode(arg0, arg1, arg2)("base", "Requested-Action",
//				ccrenumberList.get(0).getCode())
		long ccnum=-1;
		DiameterAVP avp=ccrenumberList.get(0);
		
		if(logger.isDebugEnabled()){
			logger.debug("getCCRequestType "+ avp);
		}
		if (avp.getInstanceType().equals("Integer32")) {
			DiameterInteger32AVP ccNum = (DiameterInteger32AVP) avp;
			ccnum= ccNum.getValue();
		} else if (avp.getInstanceType().equals("Unsigned32")) {
			DiameterUnsigned32AVP ccNum = (DiameterUnsigned32AVP) avp;
			ccnum= ccNum.getValue();
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCRequestType() return "+ccnum);
		}
		return ccnum;
			
	}


	/**
	 *  Retrieving a single Enumerated value from CCSessionFailover AVPs.
	 */
	@Override
	public long getCCSessionFailover() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCSessionFailover()");
		}
		ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Session_Failover);
		
		DiameterAVP avp=ccrenumberList.get(0);
			DiameterUnsigned32AVP ccNum=(DiameterUnsigned32AVP)avp;		
		return ccNum.getValue();
	}

	/**
	 *  This method returns the command code associated with this message.
	 */
	public int getCommandCode() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCommandCode()");
		}
		return (int) stackObj.getCommandCode();
	}
	
	@Override
	public void addMultipleServiceCreditControl(ArrayList<DiameterAVP> avp)
			throws RoResourceException {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()){
			logger.debug("Inside addMultipleServiceCreditControl() " +avp);
		}
		
		 getStackObject().addAVPs(avp);
		//return avp;
	}

	
//	@Override
//	public long getRequestedAction() throws RoResourceException {
//		// TODO Auto-generated method stub
//        ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.Requested_Action);
//		
//		DiameterAVP avp=ccrenumberList.get(0);
//		DiameterUnsigned32AVP ccNum=(DiameterUnsigned32AVP)avp;
//			
//		return ccNum.getValue();
//	}
	/**
	 *  Retrieving a single Enumerated value from CreditControlFailureHandling AVPs.
	 */
//	public int getCreditControlFailureHandling() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getCreditControlFailureHandling()");
//			}
//			return stackObj.getCreditControlFailureHandling();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getCreditControlFailureHandling",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Enumerated value from DirectDebitingFailureHandling AVPs.
//	 */
//	public int getDirectDebitingFailureHandling() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getDirectDebitingFailureHandling()");
//			}
//			return stackObj.getDirectDebitingFailureHandling();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getDirectDebitingFailureHandling",e);
//		}
//	}
//
//	/**
//	 *  This method the returns the enum value corroesponding to CCRequestTypeAvp.
//	 */
//	public CCRequestTypeEnum getEnumCCRequestType() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumCCRequestType()");
//			}
//			return CCRequestTypeEnum.getContainerObj(stackObj.getEnumCCRequestType());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getEnumCCRequestType",e);
//		}
//	}

//	/**
//	 *  This method the returns the enum value corroesponding to CCSessionFailoverAvp.
//	 */
//	public CCSessionFailoverEnum getEnumCCSessionFailover() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumCCSessionFailover()");
//			}
//			return CCSessionFailoverEnum.getContainerObj(stackObj.getEnumCCSessionFailover());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getEnumCCSessionFailover",e);
//		}
//	}
//
//
//	/**
//	 *  This method the returns the enum value corroesponding to CreditControlFailureHandlingAvp.
//	 */
//	public CreditControlFailureHandlingEnum getEnumCreditControlFailureHandling() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumCreditControlFailureHandling()");
//			}
//			return CreditControlFailureHandlingEnum.getContainerObj(stackObj.getEnumCreditControlFailureHandling());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getEnumCreditControlFailureHandling",e);
//		}
//	}


//	/**
//	 *  This method the returns the enum value corroesponding to DirectDebitingFailureHandlingAvp.
//	 */
//	public DirectDebitingFailureHandlingEnum getEnumDirectDebitingFailureHandling() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumDirectDebitingFailureHandling()");
//			}
//			return DirectDebitingFailureHandlingEnum.getContainerObj(stackObj.getEnumDirectDebitingFailureHandling());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getEnumDirectDebitingFailureHandling",e);
//		}
//	}
//
//
//	/**
//	 *  This method the returns the enum value corroesponding to LowBalanceIndicationAvp.
//	 */
//	public LowBalanceIndicationEnum getEnumLowBalanceIndication() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumLowBalanceIndication()");
//			}
//			return LowBalanceIndicationEnum.getContainerObj(stackObj.getEnumLowBalanceIndication());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getEnumLowBalanceIndication",e);
//		}
//	}


//	/**
//	 *  This method the returns the enum value corroesponding to RedirectHostUsageAvp.
//	 */
//	public RedirectHostUsageEnum getEnumRedirectHostUsage() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumRedirectHostUsage()");
//			}
//			return RedirectHostUsageEnum.getContainerObj(stackObj.getEnumRedirectHostUsage());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getEnumRedirectHostUsage",e);
//		}
//	}
//
//
//	/**
//	 *  Retrieving a single Grouped value from CostInformation AVPs.
//	 */
//	public CostInformationAvp getGroupedCostInformation() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedCostInformation()");
//			}
//			return new CostInformationAvp(stackObj.getGroupedCostInformation());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getGroupedCostInformation",e);
//		}
//	}

//	/**
//	 *  Retrieving multiple Grouped values from FailedAVP AVPs.
//	 */
//	public FailedAVPAvp[] getGroupedFailedAVPs() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedFailedAVPs()");
//			}
//			AvpFailedAVP[] stackAv= stackObj.getGroupedFailedAVPs();
//			FailedAVPAvp[] contAvp= new FailedAVPAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new FailedAVPAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getGroupedFailedAVPs",e);
//		}
//	}
//
//	/**
//	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
//	 */
//	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedMultipleServicesCreditControls()");
//			}
//			AvpMultipleServicesCreditControl[] stackAv= stackObj.getGroupedMultipleServicesCreditControls();
//			MultipleServicesCreditControlAvp[] contAvp= new MultipleServicesCreditControlAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new MultipleServicesCreditControlAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getGroupedMultipleServicesCreditControls",e);
//		}
//	}

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ArrayList<DiameterAVP> getGroupedProxyInfos() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getGroupedProxyInfos()");
		}
		ArrayList<DiameterAVP> stackAv= stackObj.getProxyInfoAVPs();//getGroupedProxyInfos();
		
		return stackAv;
	}

	/**
	 *  Retrieving a single Grouped value from RemainingBalance AVPs.
	 */
//	public RemainingBalanceAvp getGroupedRemainingBalance() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedRemainingBalance()");
//			}
//			return new RemainingBalanceAvp(stackObj.getGroupedRemainingBalance());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getGroupedRemainingBalance",e);
//		}
//	}

//	/**
//	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
//	 */
//	public ServiceInformationAvp getGroupedServiceInformation() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedServiceInformation()");
//			}
//			return new ServiceInformationAvp(stackObj.getGroupedServiceInformation());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getGroupedServiceInformation",e);
//		}
//	}

//	/**
//	 *  Retrieving a single Enumerated value from LowBalanceIndication AVPs.
//	 */
//	public int getLowBalanceIndication() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getLowBalanceIndication()");
//			}
//			return stackObj.getLowBalanceIndication();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getLowBalanceIndication",e);
//		}
//	}

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


	//TODO
	//	/**
	//	 *  Retrieving multiple DiameterURI values from RedirectHost AVPs.
	//	 */
	//	public URI[] getRedirectHosts() throws RoResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getRedirectHosts()");
	//			}
	//			return new URI[](stackObj.getRedirectHosts());
	//		} catch (ValidationException e) {
	//			throw new RoResourceException("Exception in getRedirectHosts",e);
	//		}
	//	}

	/**
	 *  Retrieving a single Enumerated value from RedirectHostUsage AVPs.
	 */
//	public int getRedirectHostUsage() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getRedirectHostUsage()");
//			}
//			return stackObj.getRedirectHostUsage();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getRedirectHostUsage",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Unsigned32 value from RedirectMaxCacheTime AVPs.
//	 */
//	public long getRedirectMaxCacheTime() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getRedirectMaxCacheTime()");
//			}
//			return stackObj.getRedirectMaxCacheTime();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getRedirectMaxCacheTime",e);
//		}
//	}

	/**
	 *  Retrieving a single Unsigned32 value from ResultCode AVPs.
	 */
	public String getResultCode() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getResultCode()-->");
		}
		
		if(getType()!=-1){
			if(logger.isDebugEnabled()){
				logger.debug("Inside getResultCode()--> return from answer type " + ResultCodes.getReturnCode(getType()));
			}
			return ResultCodes.getReturnCode(getType());
		}
		
		if(this.resultCode!=null){
			if(logger.isDebugEnabled()){
				logger.debug("Inside getResultCode()--> returnCode " +resultCode);
			}
			return resultCode;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Inside getResultCode()-->return AVP" +stackObj.getResultCodeAVPValue());
		}
		return stackObj.getResultCodeAVPValue();
	}

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public ArrayList<DiameterAVP> getRouteRecords() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRouteRecords()");
		}
		return stackObj.getRouteRecordAVPs();
	}
    public void setRequest(CreditControlRequest request)
    {
    	if(logger.isDebugEnabled()){
			logger.debug("Inside setRequest()");
		}
    	this.roRequest=request;
    }
	@Override
	public Request getRequest() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRequest()");
		}
		return this.roRequest;
	}
	
	
	@Override
    public void setDestinationHost(String host){
		stackObj.setDestinationHostAVPValue(host);
   }
	
//	public void setEventTimeStamp(String timestamp){
//		stackObj.set
//	}
	
	@Override
	public void setServiceContextId(String contextId){
		addDiameterOctetStringAVP("Service-Context-Id", "base",contextId);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CreditControlAnswer[stackObj=" +stackObj + "]";
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

//	@Override
//	public DiameterAVP getAvp(int avpCode, long vendorId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<DiameterAVP> getAvpList(int avpCode, long vendorId) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public AvpDiameterVendorSpecificSet getVendorSpecificAvpSet() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public CCRequestTypeEnum getEnumCCRequestType() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCRequestType()");
		}
		ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Request_Type);
		
		DiameterAVP avp=ccrenumberList.get(0);
		DiameterInteger32AVP ccNum=(DiameterInteger32AVP)avp;
			
		int cc=(int) ccNum.getValue();
		
		String name=CCRequestTypeEnum.getName(cc);
		return CCRequestTypeEnum.valueOf(name);
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
	
	@Override
	public DiameterRoMessageFactory getDiameterRoMessageFactory(){
		return this.diameterRoMsgFactory;
		
	}

//	@Override
//	public CCRequestTypeAvp addCCRequestType(CCRequestTypeEnum value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public CCSessionFailoverAvp addCCSessionFailover(CCSessionFailoverEnum value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public CreditControlFailureHandlingAvp addCreditControlFailureHandling(
//			CreditControlFailureHandlingEnum value) throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public DiameterAVP addDirectDebitingFailureHandling(
//			DirectDebitingFailureHandlingEnum value) throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}


//	@Override
//	public ProxyInfoAvp addGroupedProxyInfo() throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public long getCCRequestNumber() throws RoResourceException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public int getCCRequestType() throws RoResourceException {
//		// TODO Auto-generated method stub
//		return 0;
//	}

//	@Override
//	public int getCCSessionFailover() throws RoResourceException {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	
	

//	@Override
//	public void toXML(StringBuilder builder) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public ValidationRecord validate() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public CCRequestTypeAvp addCCRequestType(CCRequestTypeEnum value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public CCSessionFailoverAvp addCCSessionFailover(CCSessionFailoverEnum value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public CreditControlFailureHandlingAvp addCreditControlFailureHandling(
//			CreditControlFailureHandlingEnum value) throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public DiameterAVP addDirectDebitingFailureHandling(
//			DirectDebitingFailureHandlingEnum value) throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public CostInformationAvp addGroupedCostInformation()
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
//	/**
//	 *  Retrieving a single UTF8String value from SessionId AVPs.
//	 * @throws RoResourceException 
//	 */
//	public java.lang.String getSessionId() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSessionId()");
//			}
//			return stackObj.getSessionId();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getSessionId",e);
//		}
//	}
}
