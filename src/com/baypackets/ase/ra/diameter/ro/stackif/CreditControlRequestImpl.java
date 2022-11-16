
package com.baypackets.ase.ra.diameter.ro.stackif;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.ro.CreditControlAnswer;
import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.ro.enums.RequestedActionEnum;
import com.baypackets.ase.ra.diameter.ro.impl.RoMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.ro.impl.RoSession;
import com.baypackets.ase.ra.diameter.ro.utils.AvpCodes;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.ra.diameter.ro.utils.ResultCodes;
import com.baypackets.ase.ra.diameter.ro.utils.RoStackConfig;
import com.baypackets.ase.resource.ResourceException;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterException;
import fr.marben.diameter.DiameterFloat32AVP;
import fr.marben.diameter.DiameterFloat64AVP;
import fr.marben.diameter.DiameterGenericAVP;
import fr.marben.diameter.DiameterGroupedAVP;
import fr.marben.diameter.DiameterInteger32AVP;
import fr.marben.diameter.DiameterInteger64AVP;
import fr.marben.diameter.DiameterMessage;
import fr.marben.diameter.DiameterMessageFactory;
import fr.marben.diameter.DiameterOctetStringAVP;
import fr.marben.diameter.DiameterSession;
import fr.marben.diameter.DiameterStack;
import fr.marben.diameter.DiameterUnsigned32AVP;
import fr.marben.diameter.DiameterUnsigned64AVP;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public class CreditControlRequestImpl extends RoAbstractRequest implements CreditControlRequest , Constants{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(CreditControlRequestImpl.class.getName());

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	//private DiameterMessage stackObj;
	private RoSession m_roSession;
	private int retryCounter=0;
	private DiameterSession serverStackSession;
	
	private DiameterMessageFactory diameterMsgFactory;
	private DiameterRoMessageFactory diameterRoMsgFactory;
	private DiameterStack stack=null;
	
	public CreditControlRequestImpl(int type){
		super(type);
		
		stack=((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterStack();
        diameterMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		
		diameterRoMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterRoMsgFactory();

		
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

	public CreditControlRequestImpl(RoSession roSession, int type,String remoteRealm){
		super(roSession);
		
        diameterMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
		
		diameterRoMsgFactory= ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterRoMsgFactory();
		try {
			logger.debug("Inside CreditControlRequestImpl(RoSession) constructor " + roSession);
			switch (type) {
			case EVENT_REQUEST:
				logger.debug("Creating EVENT_REQUEST request");
				this.stackObj = RoMessageFactoryImpl.createCCR(roSession.getClientStackSession(), 
						CCRequestTypeEnum.EVENT_REQUEST, 
						roSession.getNextHandle(),remoteRealm);
				break;
			case INITIAL_REQUEST:
				logger.debug("Creating INITIAL_REQUEST request");
				this.stackObj = RoMessageFactoryImpl.createCCR(roSession.getClientStackSession(), 
						CCRequestTypeEnum.INITIAL_REQUEST, 
						roSession.getNextHandle(),remoteRealm);
				break;
			case UPDATE_REQUEST:
				logger.debug("Creating UPDATE_REQUEST request");
				this.stackObj = RoMessageFactoryImpl.createCCR(roSession.getClientStackSession(), 
						CCRequestTypeEnum.UPDATE_REQUEST, 
						roSession.getNextHandle(),remoteRealm);
				break;
			case TERMINATION_REQUEST:
				logger.debug("Creating TERMINATION_REQUEST request");
				this.stackObj = RoMessageFactoryImpl.createCCR(roSession.getClientStackSession(), 
						CCRequestTypeEnum.TERMINATION_REQUEST, 
						roSession.getNextHandle(),remoteRealm);
				break;
			default:
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			logger.debug("Stack object created = "+this.stackObj);
			super.setStackObject(this.stackObj);
			this.m_roSession=roSession;
			((SipApplicationSessionImpl)roSession.getApplicationSession()).getIc().setWorkQueue(getWorkQueue());
		} catch (DiameterException e) {
			logger.debug("DiameterException in creating request ",e);
			//throw new RoResourceException("ValidationException in creating request ",e);
		} catch (ResourceException e) {
			logger.debug("Wrong/Unkown request type. ",e);
			//throw new RoResourceException("UWrong/Unknown request type. ",e);
		}
	}

	public void addRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
		this.m_roSession.addRequest(this);
	}
	////////////////////////////////////////////////////////////////////
	///////////// MARBEN Credit Control Request API STARTS ///////////
	////////////////////////////////////////////////////////////////////

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
			
//			if(groupAvps!=null){
//			   avpIn.setValue(groupAvps);
//			}
			avp= new AvpDiameterGrouped(avpIn,diameterMsgFactory);
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
	/**
	 *  Adding CCCorrelationId AVP of type OctetString to the message.
	 */
	public void addCCCorrelationId(byte[] value, boolean mFlag,String vendorName) throws RoResourceException {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCCorrelationId()");
		}
		DiameterOctetStringAVP avp1=diameterMsgFactory.createOctetStringAVP("CC-Correlation-Id", vendorName,value);
		getStackObject().add(avp1);
		
	}

	@Override
	/**
	 *  Adding CCCorrelationId AVP of type OctetString to the message.
	 */
	public void addCCCorrelationId(java.lang.String value, boolean mFlag,String vendorName) throws RoResourceException {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCCorrelationId()");
		}
		DiameterOctetStringAVP avp1=diameterMsgFactory.createOctetStringAVP("CC-Correlation-Id", vendorName,value);
		
		getStackObject().add(avp1);
		
	}

	@Override
	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
	public void addCCRequestNumber(long value) throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCRequestNumber()");
		}
		DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
				"CC-Request-Number", "base", (int)value);
		 getStackObject().add(avpIn);
	}

	@Override
	/**
	 *  AddingaddCCRequestType AVP  to the message.
	 */
	public void addCCRequestType(int  type) throws RoResourceException {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside addCCRequestNumber()");
		}
		DiameterUnsigned32AVP avpIn=diameterMsgFactory.createUnsigned32AVP(
				"CC-Request-Type", "base", (int)type);
		 getStackObject().add(avpIn);
	}

//	/**
//	 *  Adding EventTimestamp AVP of type Time to the message.
//	 */
//	public EventTimestampAvp addEventTimestamp(java.util.Date value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addEventTimestamp()");
//			}
//			return new EventTimestampAvp(stackObj.addEventTimestamp(value));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addEventTimestamp",e);
//		}
//	}

	/**
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
//
//	/**
//	 *  Adding ProxyInfo AVP of type Grouped to the message.
//	 */
//	public ProxyInfoAvp addGroupedProxyInfo() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedProxyInfo()");
//			}
//			return new ProxyInfoAvp(stackObj.addProxyInfo());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedProxyInfo",e);
//		}
//	}
//
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
//	 *  Adding SubscriptionId AVP of type Grouped to the message.
//	 */
//	public SubscriptionIdAvp addGroupedSubscriptionId() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedSubscriptionId()");
//			}
//			return new SubscriptionIdAvp(stackObj.addGroupedSubscriptionId());
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedSubscriptionId",e);
//		}
//	}
//
//	/**
//	 *  Adding UserEquipmentInfo AVP of type Grouped to the message.
//	 */
//	public UserEquipmentInfoAvp addGroupedUserEquipmentInfo(boolean mFlag) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedUserEquipmentInfo()");
//			}
//			return new UserEquipmentInfoAvp(stackObj.addGroupedUserEquipmentInfo(mFlag));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addGroupedUserEquipmentInfo",e);
//		}
//	}
//
//	/**
//	 *  Adding MultipleServicesIndicator AVP of type Enumerated to the message.
//	 */
//	public MultipleServicesIndicatorAvp addMultipleServicesIndicator(MultipleServicesIndicatorEnum value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addMultipleServicesIndicator() with "+value);
//			}
//			logger.debug("TODO STACK CCR OBJECT IS  "+stackObj);
//			EnumMultipleServicesIndicator stkVal = MultipleServicesIndicatorEnum.getStackObj(value); 
//			if(stkVal instanceof EnumMultipleServicesIndicator) {
//				logger.debug("TRUE INSTANCE  "+MultipleServicesIndicatorEnum.getStackObj(value));
//			}else{
//				logger.debug("FALSE NOT AN INSTANCE ");
//			}
//			if(stkVal==EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED){
//				logger.debug(" NOW WHAT TO DO NEXT ");
//			}else{
//				logger.debug("OH SHIT.......................");
//			}
//			logger.debug("TODO STACK OBJECT IS  "+MultipleServicesIndicatorEnum.getStackObj(value));
//			//logger.debug("TODO STACK OBJECT AVP IS  "+stackObj.addMultipleServicesIndicator(MultipleServicesIndicatorEnum.getStackObj(value)));
//			return new MultipleServicesIndicatorAvp(stackObj.addMultipleServicesIndicator(stkVal));
//			//return new MultipleServicesIndicatorAvp(stackObj.addMultipleServicesIndicator(MultipleServicesIndicatorEnum.getStackObj(value)));
//			//return new MultipleServicesIndicatorAvp(stackObj.addMultipleServicesIndicator(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addMultipleServicesIndicator",e);
//		}
//	}
//
//	/**
//	 *  Adding OriginStateId AVP of type Unsigned32 to the message.
//	 */
//	public OriginStateIdAvp addOriginStateId(long value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addOriginStateId()");
//			}
//			return new OriginStateIdAvp(stackObj.addOriginStateId(value));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addOriginStateId",e);
//		}
//	}

//	/**
//	 *  Adding RequestedAction AVP of type Enumerated to the message.
//	 */
//	public RequestedActionAvp addRequestedAction(RequestedActionEnum value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addRequestedAction()");
//			}
//			return new RequestedActionAvp(stackObj.addRequestedAction(RequestedActionEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addRequestedAction",e);
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
//	 *  Adding ServiceContextId AVP of type UTF8String to the message.
//	 */
//	public ServiceContextIdAvp addServiceContextId(java.lang.String value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addServiceContextId() "+this.stackObj);
//			}
//			return new ServiceContextIdAvp(this.stackObj.addServiceContextId(value));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addServiceContextId",e);
//		}
//	}
//
//	/**
//	 *  Adding TerminationCause AVP of type Enumerated to the message.
//	 */
//	public TerminationCauseAvp addTerminationCause(TerminationCauseEnum value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addTerminationCause()");
//			}
//			return new TerminationCauseAvp(stackObj.addTerminationCause(TerminationCauseEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addTerminationCause",e);
//		}
//	}

//	/**
//	 *  Adding UserName AVP of type UTF8String to the message.
//	 */
//	public UserNameAvp addUserName(java.lang.String value) throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addUserName()");
//			}
//			return new UserNameAvp(stackObj.addUserName(value));
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in addUserName",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Enumerated value from AoCRequestType AVPs.
//	 */
//	public int getAoCRequestType() throws RoResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getAoCRequestType()");
//			}
//			return stackObj.getAoCRequestType();
//		} catch (ValidationException e) {
//			throw new RoResourceException("Exception in getAoCRequestType",e);
//		}
//	}

	//	/**
	//	 *  Retrieving application id associated with this request.
	//	 * @throws RoResourceException 
	//	 */
	//	public long getApplicationId() throws RoResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getApplicationId()");
	//			}
	//			return stackObj.getApplicationId();
	//		} catch (ValidationException e) {
	//			throw new RoResourceException("Exception in getApplicationId",e);
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
//
	@Override
	/**
	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
	 */
	public java.lang.String getCCCorrelationId() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getCCCorrelationId()");
		}
		
		ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Request_Type);
		
		DiameterAVP avp=ccrenumberList.get(0);
		DiameterOctetStringAVP ccNum=(DiameterOctetStringAVP)avp;
			
		return ccNum.getStringValue();
	}

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


	@Override
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

	@Override
	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public ArrayList<DiameterAVP> getRouteRecords() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRouteRecords()");
		}
		return stackObj.getRouteRecordAVPs();
	}


	public CreditControlAnswer createAnswer(String returnCode) throws RoResourceException {

		if(logger.isDebugEnabled()){
			logger.debug("Inside createAnswer(long) findReturnCodeName From stack for code  object--> " +this.getStackObj());
		}

		
		DiameterMessage answer=null;
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Create  CCA --> from "+ returnCode);
			}
			
			answer = diameterRoMsgFactory.createCreditControlAnswer(
					returnCode, this.getStackObj());//stack.findReturnCodeName("3gpp", l)
			
			/* Send the message through the session */
			logger.info("Created  CCA : " + answer.toString());
			//answer.setResultCodeAVPValue(ResultCodeAvp.getName((int)l));
			
			if(RoStackConfig.isStateless()) {
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
			
			
//			stack.EnumCode(arg0, arg1, arg2)("base", "Requested-Action",
//					ccrenumberList.get(0).getCode())
//            ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Request_Type);
//			
//			if(logger.isDebugEnabled()){
//				logger.debug("setCCRequestType "+ ccrenumberList.toString());
//			}
//			DiameterAVP avp=ccrenumberList.get(0);
//			answer.add(avp);
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
		
//			session.sendMessage(answer);
//			
//			DiameterMessage answer=null;
//
//			answer = stackObj.createAnswer(l);
//
//			answer.addAuthApplicationId(stackObj.getApplicationId());
//
//			//Adding request type and number, so client will know on what we answered.
//			answer.addCCRequestNumber(stackObj.getCCRequestNumber());
//			answer.addCCRequestType(stackObj.getEnumCCRequestType());
//
//			if(request.get!= EnumCCRequestType.TERMINATION_REQUEST) {
//				//Adding Multiple-Service-Credit-Control
//				//Here a response of granted units will be sent for each MSSC in the request.
//				//We will determine granted units per Rating Grouped MSCC request, in reality it does not have to be so.
//				//Checking for optional MULTIPLE SERVICES INDICATOR flag if present and set as MULTIPLE_SERVICES_SUPPORTED then perform this operation of adding MSCC in answer.
//				int indicator=EnumMultipleServicesIndicator.MULTIPLE_SERVICES_NOT_SUPPORTED.getCode();
//				try{
//					indicator=stackObj.getMultipleServicesIndicator();
//				}catch (ValidationException e) {
//					logger.debug("ValidationException in getMultipleServicesIndicator using default as MULTIPLE_SERVICES_NOT_SUPPORTED ");
//				}
//				if(EnumMultipleServicesIndicator.isValid(indicator)&& EnumMultipleServicesIndicator.fromCode(indicator).equals(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED)){
//					AvpMultipleServicesCreditControl[] aMSCC = stackObj.getGroupedMultipleServicesCreditControls();
//					for(int i=0; i<aMSCC.length; i++){
//						fillMSCCAnswerPerRatingGroup(answer,aMSCC[i]);
//					}
//				}				
//			}
		CreditControlAnswerImpl response=new CreditControlAnswerImpl(answer);
		response.setResultCode(returnCode);
		response.setProtocolSession(this.getProtocolSession());
		response.setRequest(this);
		return response;
	}



	public CreditControlAnswer createAnswer(long vendorId, int experimentalResultCode)
	throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside createAnswer(long vendorId, long experimentalResultCode)");
		}
		
		DiameterMessage answer=null;
		try {
			answer = diameterRoMsgFactory.createCreditControlAnswer(
					ResultCodes.getReturnCode((int)experimentalResultCode), this.getStackObj());
			
			/* Send the message through the session */
			logger.info("Sending CCA : " + answer.toString());
			//answer.setResultCodeAVPValue(ResultCodeAvp.getName((int)l));
			ArrayList<DiameterAVP> list=new ArrayList<DiameterAVP>();
			
			
			
			DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
					"Vendor-Id", "3GPP", (int)vendorId);
			
			list.add(avpIn);
			answer.addAVPs(list);
			
			if(RoStackConfig.isStateless()) {
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

//			MessageCCA answer=null;
//
//			answer = stackObj.createAnswer(vendorId,experimentalResultCode);
//
//			answer.addAuthApplicationId(stackObj.getApplicationId());
//
//			//Adding request type and number, so client will know on what we answered.
//			answer.addCCRequestNumber(stackObj.getCCRequestNumber());
//			answer.addCCRequestType(stackObj.getEnumCCRequestType());
//
//			//Adding Multiple-Service-Credit-Control
//			//Here a response of granted units will be sent for each MSSC in the request.
//			//We will determine granted units per Rating Grouped MSCC request, in reality it does not have to be so.
//			//Checking for optional MULTIPLE SERVICES INDICATOR flag if present and set as MULTIPLE_SERVICES_SUPPORTED then perform this operation of adding MSCC in answer.
//			int indicator=EnumMultipleServicesIndicator.MULTIPLE_SERVICES_NOT_SUPPORTED.getCode();
//			try{
//				indicator=stackObj.getMultipleServicesIndicator();
//			}catch (ValidationException e) {
//				logger.debug("ValidationException in getMultipleServicesIndicator using default as MULTIPLE_SERVICES_NOT_SUPPORTED");
//			}
//			if(EnumMultipleServicesIndicator.isValid(indicator)&& EnumMultipleServicesIndicator.fromCode(indicator).equals(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED)){
//				AvpMultipleServicesCreditControl[] aMSCC = stackObj.getGroupedMultipleServicesCreditControls();
//				for(int i=0; i<aMSCC.length; i++){
//					fillMSCCAnswerPerRatingGroup(answer,aMSCC[i]);
//				}
//			}				
		CreditControlAnswerImpl response=new CreditControlAnswerImpl(answer);
		response.setRequest(this);
		return response;

	}

	//Handle answer for each service this server supports.
	//
//	private void fillMSCCAnswerPerRatingGroup(MessageCCA answer, AvpMultipleServicesCreditControl gRequestMSCC) throws ValidationException
//	{
//		long[] serviceIdentifers =gRequestMSCC.getServiceIdentifiers();
//		//Adding Multiple-Service-Credit-Control to answer
//		/** adding Avps from request, so answer could be identified by client properly **/
//		AvpMultipleServicesCreditControl gAvpMSCC = answer.addGroupedMultipleServicesCreditControl();
//		//Identifying the requested service
//		for(int i=0;i<serviceIdentifers.length;i++){
//			gAvpMSCC.addServiceIdentifier(serviceIdentifers[i]);
//		}
//
//		//Adding RequestedServiceUnit to it. 
//		AvpRequestedServiceUnit mscc_gAvpRSU = gAvpMSCC
//		.addGroupedRequestedServiceUnit();
//		mscc_gAvpRSU.addCCTotalOctets(gRequestMSCC
//				.getGroupedRequestedServiceUnit().getCCTotalOctets());
//		gAvpMSCC.addResultCode(ResultCode.SUCCESS);
//
//		if(gRequestMSCC.getRatingGroup()==1) {
//			//Adding GrantedServiceUnit
//			AvpGrantedServiceUnit gAvpGSU = gAvpMSCC.addGroupedGrantedServiceUnit();
//			//Request Granted, after the usage of the following granted resource another CCR MUST be sent by client.
//			gAvpGSU.addCCTotalOctets(gRequestMSCC.getGroupedRequestedServiceUnit()
//					.getCCTotalOctets());
//		}
//		else if(gRequestMSCC.getRatingGroup()==2){
//			//Adding GrantedServiceUnit
//			AvpGrantedServiceUnit gAvpGSU = gAvpMSCC.addGroupedGrantedServiceUnit();
//			//Request Granted, after the usage of the following granted resource another CCR MUST be sent by client.
//			gAvpGSU.addCCTotalOctets(gRequestMSCC.getGroupedRequestedServiceUnit()
//					.getCCTotalOctets());
//			//In this case, we also add a validation time.
//			//After the validation time expires the client MUST send an update request.
//			gAvpMSCC.addValidityTime(3000L);
//
//
//		}
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CreditControlRequest[stackObj=" + stackObj + "]";
	}

	@Override
	public void toXML(StringBuilder builder) {
		// TODO Auto-generated method stub
		
	}


//	@Override
//	public AvpDiameterVendorSpecificSet getVendorSpecificAvpSet() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public AoCRequestTypeAvp addAoCRequestType(AoCRequestTypeEnum value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public AuthApplicationIdAvp addAuthApplicationId(long value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}


//	@Override
//	public ProxyInfoAvp addGroupedProxyInfo() throws RoResourceException {
//		// TODO Auto-generated method stub
//		this.stackObj.set
//		return null;
//	}


	@Override
	public void addRequestedAction(int value)
			throws RoResourceException {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()){
			logger.debug("Inside addRequestedAction() " +value);
		}
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP("Requested-Action", "base", (int)value);
		// avp= new AvpDiameterInteger32(avpIn);
		 getStackObject().add(avpIn);
		//return avp;
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
//	public RouteRecordAvp addRouteRecord(String value)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public void  setSessionIDAVP(String sessionID){
		if(logger.isDebugEnabled()){
			logger.debug("setSessionIDAVP "+ sessionID);
		}
		
		getStackObject().setSessionIdAVPValue(sessionID);
	}

	@Override
	public void addServiceContextId(String value)
			throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside addServiceContextId() "+value);
		}
		DiameterOctetStringAVP avpIn=diameterMsgFactory.createOctetStringAVP(name,"base",value);
		
		 getStackObject().add(avpIn);
	}

//	@Override
//	public CreditControlAnswer createAnswer(long resultCode)
//			throws RoResourceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public int getAoCRequestType() throws RoResourceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAuthApplicationId() throws RoResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("getAuthApplicationId Return 4");
		}
		return 4;
	}


	@Override
	public String getServiceContextId() throws RoResourceException {
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
	public long getRequestedAction() throws RoResourceException {
		// TODO Auto-generated method stub
        ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.Requested_Action);
		
		DiameterAVP avp=ccrenumberList.get(0);
		DiameterUnsigned32AVP ccNum=(DiameterUnsigned32AVP)avp;
			
		return ccNum.getValue();
	}

	@Override
	public ValidationRecord validate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CCRequestTypeEnum getEnumCCRequestType() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getEnumCCRequestType()");
		}
       ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.CC_Request_Type);
		
		if(logger.isDebugEnabled()){
			logger.debug("getEnumCCRequestType "+ ccrenumberList.toString());
		}
		
//		stack.EnumCode(arg0, arg1, arg2)("base", "Requested-Action",
//				ccrenumberList.get(0).getCode())
		
		DiameterAVP avp=ccrenumberList.get(0);
		
		DiameterUnsigned32AVP ccNum=(DiameterUnsigned32AVP)avp;
		String name=CCRequestTypeEnum.getName((int)ccNum.getValue());
		return CCRequestTypeEnum.valueOf(name);
	}

	@Override
	public RequestedActionEnum getEnumRequestedAction() {
		// TODO Auto-generated method stub
        ArrayList<DiameterAVP> ccrenumberList=stackObj.getAVP(AvpCodes.Requested_Action);
		
		DiameterAVP avp=ccrenumberList.get(0);
		DiameterInteger32AVP ccNum=(DiameterInteger32AVP)avp;
			
		return RequestedActionEnum.fromCode((int)ccNum.getValue());
	}

	@Override
	public byte getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setStatelessSenderOriginHost(String nextHop) {
		// TODO Auto-generated method stub
		senderOriginHoststatelless=nextHop;
	}

	public String getStatelessSenderOriginHost() {
		// TODO Auto-generated method stub
		return senderOriginHoststatelless;
	}
	private String senderOriginHoststatelless;
	
	
	@Override
	public DiameterRoMessageFactory getDiameterRoMessageFactory(){
		return this.diameterRoMsgFactory;
		
	}
	
	@Override
     public void setDestinationHost(String host){
		stackObj.setDestinationHostAVPValue(host);
    }
	
//	public void setEventTimeStamp(String timestamp){
//		stackObj.set
//	}
	
	public void setServiceContextId(String contextId){
		addDiameterOctetStringAVP("Service-Context-Id", "base",contextId);
	}
	
//	public void setUserName(String username){
//		addDiameterOctetStringAVP("User-Name", "3GPP",contextId);
//	}
//	
//	public void setOriginStateId(String stateId);
	
}

