/**
 * Filename:	CreditControlRequestImpl.java
 * Created On:	04-Oct-2006
 */
package com.baypackets.ase.ra.ro.stackif;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.ra.ro.impl.RoSession;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.resource.ResourceException;

import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationSet;

import com.condor.ro.roclient.request.*;
import com.condor.ro.rocommon.AAADestinationInfo;

public final class CreditControlRequestImpl
	extends AbstractCreditControlMessage
	implements CreditControlRequest {

	private static Logger logger = Logger.getLogger(CreditControlRequestImpl.class);

	private int _reqType = -1;
	private Object _stackReq;

	// Ro Fields
	private short _reqAction = -1;
	private String _serviceCtxtId;
	private String _userName;
	private int _evtTimestamp = -1;
	private List _subscriptionIdList;
	private short _termCause = -1;
	private short _mulServInd = -1;
	private List _mulServCCList;
	private UserEquipmentInfo _ueInfo;
	private DiamIdent _destHost = null;
	private DiamIdent _destRealm = null;

	public CreditControlRequestImpl(RoSession roSession, int reqType) {
		super(roSession, reqType, false);

		roSession.addRequest(this);

		this._reqType = reqType;

		switch(this._reqType) {
			case Constants.RO_FIRST_INTERROGATION:
			break;

			case Constants.RO_INTERMEDIATE_INTERROGATION:
			break;

			case Constants.RO_FINAL_INTERROGATION:
			break;

			case Constants.RO_DIRECT_DEBITING:
				this._reqAction = Constants.CCRA_DIRECT_DEBITING;
			break;

			case Constants.RO_REFUND_ACCOUNT:
				this._reqAction = Constants.CCRA_REFUND_ACCOUNT;
			break;

			case Constants.RO_CHECK_BALANCE:
				this._reqAction = Constants.CCRA_CHECK_BALANCE;
			break;

			case Constants.RO_PRICE_ENQUERY:
				this._reqAction = Constants.CCRA_PRICE_ENQUERY;
			break;

			default:
				throw new IllegalArgumentException("Unknown request type : " + reqType);
		}
	}

	public String getServiceContextId() {
		return this._serviceCtxtId;
	}

	public String getUserName() {
		return this._userName;
	}

	public long getOriginStateId() {
		throw new IllegalStateException("Not supported by stack");
	}

	public int getEventTimestamp() {
		return this._evtTimestamp;
	}

	public List getSubscriptionIdList() {
		return this._subscriptionIdList;
	}

	public short getTerminationCause() {
		return this._termCause;
	}

	public short getRequestedAction() {
		return this._reqAction;
	}

	public short getMultipleServicesIndicator() {
		return this._mulServInd;
	}

	public List getMultipleServicesCreditControlList() {
		return this._mulServCCList;
	}

	public UserEquipmentInfo getUserEquipmentInfo() {
		return this._ueInfo;
	}

	public void setServiceContextId(String servContextId)
		throws RoResourceException {
		this.checkReadOnly();

		this._serviceCtxtId = servContextId;
	}

	public void setUserName(String username)
		throws RoResourceException {
		this.checkReadOnly();

		this._userName = username;
	}

	public void setOriginStateId(long origStateId)
		throws RoResourceException {
		throw new RoResourceException("Not supported by stack");
	}

	public void setEventTimestamp(int timestamp)
		throws RoResourceException {
		this.checkReadOnly();

		this._evtTimestamp = timestamp;
	}

	public void addSubscriptionId(SubscriptionId subscriptionId)
		throws RoResourceException {
		this.checkReadOnly();

		if(this._subscriptionIdList == null) {
			this._subscriptionIdList = new LinkedList();
		}
		this._subscriptionIdList.add(subscriptionId);
	}
	
	public void setTerminationCause(short termCause)
		throws RoResourceException {
		this.checkReadOnly();

		this._termCause = termCause;
	}
	
	public void setMultipleServicesIndicator(short mulServIndicator)
		throws RoResourceException {
		this.checkReadOnly();

		this._mulServInd = mulServIndicator;
	}

	public void addMultipleServicesCreditControl(
							CCRMultipleServicesCreditControl mulServCC)
		throws RoResourceException {
		this.checkReadOnly();

		if(this._mulServCCList == null) {
			this._mulServCCList = new LinkedList();
		}
		this._mulServCCList.add(mulServCC);
	}

	public void setUserEquipmentInfo(UserEquipmentInfo ueInfo)
		throws RoResourceException {
		this.checkReadOnly();

		this._ueInfo = ueInfo;
	}

	public int getType() {
		return this._reqType;
	}

	public DiamIdent getDestinationHost() {
		return this._destHost;
	}

	public DiamIdent getDestinationRealm() {
		return this._destRealm;
	}

	public void setDestinationHost(DiamIdent dest) {
		this._destHost = dest;
	}

	public void setDestinationRealm(DiamIdent dest) {
		this._destRealm = dest;
	}

	public Response createResponse(int type) throws ResourceException {
		logger.debug("inside createResponse():");
		throw new ResourceException("Response creation not allowed");
	}

	Object getStackImpl() {
		if(this._stackReq != null) {
			return this._stackReq;
		}

		switch(this._reqType) {
			case Constants.RO_FIRST_INTERROGATION:
				RoFirstInterogationReq firstInt = new RoFirstInterogationReq();

				// Copy Multiple Services Credit Control objects
				if(this._mulServCCList != null) {
					Iterator iter = this._mulServCCList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						CCRMultipleServicesCreditControlImpl mulServ =
								(CCRMultipleServicesCreditControlImpl)iter.next();
						firstInt.setAAACCRMulServices(mulServ.getStackImpl(), count++);
					}
					if(logger.isDebugEnabled()) {
						logger.debug("No of multiple service is: " + count);
					}
					firstInt.setNoOfMultipleServices((new Short(count)).byteValue());
				} else  {
					firstInt.setNoOfMultipleServices((byte)0);
				}

				// Copy Event Timestamp
				if(logger.isDebugEnabled()) {
					logger.debug("Event timestamp is: " + this._evtTimestamp);
				}
				if(this._evtTimestamp != -1) {
					firstInt.setEventTimeStamp(Integer.toString(this._evtTimestamp));
					firstInt.setIsEventTimeStampPresent(true);
				} else {
					firstInt.setIsEventTimeStampPresent(false);
				}

				// Copy UE Info
				if(this._ueInfo != null) {
					firstInt.setUserEqpmtInfoType(this._ueInfo.getUserEquipmentInfoType());
					firstInt.setUserEqpmtInfoValue(new String(this._ueInfo.getUserEquipmentInfoValue()));
					firstInt.setIsUEIPresent(true);
				} else {
					firstInt.setIsUEIPresent(false);
				}

				// Copy Service Information
				if(this.getServiceInformation() != null) {
					firstInt.setServiceInfo(
						((ServiceInformationImpl)this.getServiceInformation()).getStackImpl());
					firstInt.setIsServiceInfoPresent(true);
				}

				// Copy Subscription Ids
				if(this._subscriptionIdList != null) {
					Iterator iter = this._subscriptionIdList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						SubscriptionIdImpl subId = (SubscriptionIdImpl)iter.next();
						firstInt.setSubscriptionId(subId.getStackImpl(), count++);
					}
					firstInt.setNoOfSubscriptionIds((new Short(count)).byteValue());
				} else {
					firstInt.setNoOfSubscriptionIds((byte)0);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				firstInt.setServiceContextId(this._serviceCtxtId);

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				firstInt.setUserName(this._userName);

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					firstInt.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					firstInt.setIsCustomAvpPresent(true);
				}

				this._stackReq = firstInt;
			break;

			case Constants.RO_INTERMEDIATE_INTERROGATION:
				RoUpdateInterogationReq updateInt = new RoUpdateInterogationReq();

				// Copy Multiple Services Credit Control objects
				if(this._mulServCCList != null) {
					Iterator iter = this._mulServCCList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						CCRMultipleServicesCreditControlImpl mulServ =
								(CCRMultipleServicesCreditControlImpl)iter.next();
						updateInt.setAAACCRMulServices(mulServ.getStackImpl(), count++);
					}
					if(logger.isDebugEnabled()) {
						logger.debug("No of multiple service is: " + count);
					}
					updateInt.setNoOfMultipleServ((new Short(count)).byteValue());
				} else  {
					updateInt.setNoOfMultipleServ((byte)0);
				}

				// Copy Event Timestamp
				if(logger.isDebugEnabled()) {
					logger.debug("Event timestamp is: " + this._evtTimestamp);
				}
				if(this._evtTimestamp != -1) {
					updateInt.setEventTimeStamp(Integer.toString(this._evtTimestamp));
					updateInt.setIsEventTimeStampPresent(true);
				} else {
					updateInt.setIsEventTimeStampPresent(false);
				}

				// Copy Service Information
				if(this.getServiceInformation() != null) {
					updateInt.setServiceInfo(
						((ServiceInformationImpl)this.getServiceInformation()).getStackImpl());
					updateInt.setIsServiceInfoPresent(true);
				}

				// Copy Subscription Ids
				if(this._subscriptionIdList != null) {
					Iterator iter = this._subscriptionIdList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						SubscriptionIdImpl subId = (SubscriptionIdImpl)iter.next();
						updateInt.setSubscriptionId(subId.getStackImpl(), count++);
					}
					updateInt.setNoOfSubscriptionIds((new Short(count)).byteValue());
				} else {
					updateInt.setNoOfSubscriptionIds((byte)0);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				if(this._serviceCtxtId != null) {
					updateInt.setServiceCtxId(this._serviceCtxtId);
					updateInt.setIsServiceCtxIdPresent(true);
				} else {
					updateInt.setIsServiceCtxIdPresent(false);
				}

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				updateInt.setUserName(this._userName);

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					updateInt.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					updateInt.setIsCustomAvpPresent(true);
				}

				this._stackReq = updateInt;
			break;

			case Constants.RO_FINAL_INTERROGATION:
				RoFinalInterogationReq finalInt = new RoFinalInterogationReq();

				// Copy Multiple Services Credit Control objects
				if(this._mulServCCList != null) {
					Iterator iter = this._mulServCCList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						CCRMultipleServicesCreditControlImpl mulServ =
								(CCRMultipleServicesCreditControlImpl)iter.next();
						finalInt.setAAACCRMulServices(mulServ.getStackImpl(), count++);
					}
					if(logger.isDebugEnabled()) {
						logger.debug("No of multiple service is: " + count);
					}
					finalInt.setNoOfMultipleServ((new Short(count)).byteValue());
				} else  {
					finalInt.setNoOfMultipleServ((byte)0);
				}

				// Copy Event Timestamp
				if(logger.isDebugEnabled()) {
					logger.debug("Event timestamp is: " + this._evtTimestamp);
				}
				if(this._evtTimestamp != -1) {
					finalInt.setEventTimeStamp(Integer.toString(this._evtTimestamp));
					finalInt.setIsEventTimeStampPresent(true);
				} else {
					finalInt.setIsEventTimeStampPresent(false);
				}

				// Copy Service Information
				if(this.getServiceInformation() != null) {
					finalInt.setServiceInfo(
						((ServiceInformationImpl)this.getServiceInformation()).getStackImpl());
					finalInt.setIsServiceInfoPresent(true);
				}

				// Copy Subscription Ids
				if(this._subscriptionIdList != null) {
					Iterator iter = this._subscriptionIdList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						SubscriptionIdImpl subId = (SubscriptionIdImpl)iter.next();
						finalInt.setSubscriptionId(subId.getStackImpl(), count++);
					}
					finalInt.setNoOfSubscriptionIds((new Short(count)).byteValue());
				} else {
					finalInt.setNoOfSubscriptionIds((byte)0);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				finalInt.setServiceContextId(this._serviceCtxtId);

				// Copy Termination Cause
				if(logger.isDebugEnabled()) {
					logger.debug("Termination Cause is: " + this._termCause);
				}
				if(this._termCause >= 0) {
					finalInt.setTerminationCause(this._termCause);
					finalInt.setIsTermCausePresent(true);
				} else {
					finalInt.setIsTermCausePresent(false);
				}

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				finalInt.setUserName(this._userName);

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					finalInt.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					finalInt.setIsCustomAvpPresent(true);
				}

				this._stackReq = finalInt;
			break;

			case Constants.RO_DIRECT_DEBITING:
				RoDirectDebitingReq dirDebit = new RoDirectDebitingReq();

				// Copy Multiple Services Credit Control objects
				logger.debug("Copy Multiple Services Credit Control objects");
				if(this._mulServCCList != null) {
					Iterator iter = this._mulServCCList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						CCRMultipleServicesCreditControlImpl mulServ =
								(CCRMultipleServicesCreditControlImpl)iter.next();
						dirDebit.setAAACCRMulServices(mulServ.getStackImpl(), count++);
					}
					if(logger.isDebugEnabled()) {
						logger.debug("No of multiple service is: " + count);
					}
					dirDebit.setNoOfMultipleServ((new Short(count)).byteValue());
				} else  {
					dirDebit.setNoOfMultipleServ((byte)0);
				}
				// TODO - Sort out setRoSerUnit

				// Copy Event Timestamp
				if(logger.isDebugEnabled()) {
					logger.debug("Event timestamp is: " + this._evtTimestamp);
				}
				if(this._evtTimestamp != -1) {
					dirDebit.setEventTimeStamp(Integer.toString(this._evtTimestamp));
					dirDebit.setIsEventTimeStampPresent(true);
				} else {
					dirDebit.setIsEventTimeStampPresent(false);
				}

				// Copy Service Information
				if(this.getServiceInformation() != null) {
					dirDebit.setServiceInfo(
						((ServiceInformationImpl)this.getServiceInformation()).getStackImpl());
					dirDebit.setIsServiceInfoPresent(true);
					// TODO - Sort out setServiceId
				}

				// Copy Subscription Ids
				if(this._subscriptionIdList != null) {
					Iterator iter = this._subscriptionIdList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						SubscriptionIdImpl subId = (SubscriptionIdImpl)iter.next();
						logger.debug("stack object is ..."+ subId.getStackImpl());
						dirDebit.setSubscriptionId(subId.getStackImpl(), count++);
					}
					dirDebit.setSubscriptionIdOcr((new Short(count)).byteValue());
				} else {
					dirDebit.setSubscriptionIdOcr((byte)0);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				dirDebit.setServiceContextId(this._serviceCtxtId);

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				if(this._userName != null) {
					dirDebit.setUserName(this._userName);
					dirDebit.setIsUserNamePresent(true);
				} else {
					dirDebit.setIsUserNamePresent(false);
				}

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					dirDebit.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					dirDebit.setIsCustomAvpPresent(true);
				}

				this._stackReq = dirDebit;
			break;

			case Constants.RO_REFUND_ACCOUNT:
				RoRefundServiceReq refundAccnt = new RoRefundServiceReq();

				// Copy Multiple Services Credit Control objects
				if(this._mulServCCList != null) {
					Iterator iter = this._mulServCCList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						CCRMultipleServicesCreditControlImpl mulServ =
								(CCRMultipleServicesCreditControlImpl)iter.next();
						refundAccnt.setAAACCRMulServices(mulServ.getStackImpl(), count++);
					}
					if(logger.isDebugEnabled()) {
						logger.debug("No of multiple service is: " + count);
					}
					refundAccnt.setNoOfMultipleServ((new Short(count)).byteValue());
				} else  {
					refundAccnt.setNoOfMultipleServ((byte)0);
				}

				// Copy Service Information
				if(this.getServiceInformation() != null) {
					refundAccnt.setServiceInfo(
						((ServiceInformationImpl)this.getServiceInformation()).getStackImpl());
					refundAccnt.setIsServiceInfoPresent(true);
				}

				// Copy Subscription Ids
				if(this._subscriptionIdList != null) {
					Iterator iter = this._subscriptionIdList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						SubscriptionIdImpl subId = (SubscriptionIdImpl)iter.next();
						refundAccnt.setSubscriptionId(subId.getStackImpl(), count++);
					}
					refundAccnt.setSubscriptionIdOcr((new Short(count)).byteValue());
				} else {
					refundAccnt.setSubscriptionIdOcr((byte)0);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				refundAccnt.setServiceContextId(this._serviceCtxtId);

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				if(this._userName != null) {
					refundAccnt.setUserName(this._userName);
					refundAccnt.setIsUserNamePresent(true);
				} else {
					refundAccnt.setIsUserNamePresent(false);
				}

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					refundAccnt.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					refundAccnt.setIsCustomAvpPresent(true);
				}

				this._stackReq = refundAccnt;
			break;

			case Constants.RO_CHECK_BALANCE:
				RoBalanceCheckReq balCheck = new RoBalanceCheckReq();

				// Copy Subscription Ids
				if(this._subscriptionIdList != null) {
					Iterator iter = this._subscriptionIdList.iterator();
					short count = 0;
					while(iter.hasNext()) {
						SubscriptionIdImpl subId = (SubscriptionIdImpl)iter.next();
						balCheck.setSubscriptionId(subId.getStackImpl(), count++);
					}
					balCheck.setSubscriptionIdOcr((new Short(count)).byteValue());
				} else {
					balCheck.setSubscriptionIdOcr((byte)0);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				balCheck.setServiceContextId(this._serviceCtxtId);
				balCheck.setServiceContextIdPBit(false); // Not encoded

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				if(this._userName != null) {
					balCheck.setUserName(this._userName);
					balCheck.setIsUserNamePresent(true);
					balCheck.setUserNamePBit(false); // Not encoded
				} else {
					balCheck.setIsUserNamePresent(false);
				}

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					balCheck.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					balCheck.setIsCustomAvpPresent(true);
				}

				this._stackReq = balCheck;
			break;

			case Constants.RO_PRICE_ENQUERY:
				RoServicePriceEnqRequest priceEnq = new RoServicePriceEnqRequest();

				// Copy Service Information
				if(this.getServiceInformation() != null) {
					priceEnq.setServiceInfo(
						((ServiceInformationImpl)this.getServiceInformation()).getStackImpl());
					priceEnq.setIsServiceInfoPresent(true);
				}

				// Copy Service Context Id
				if(logger.isDebugEnabled()) {
					logger.debug("service context id is: " + this._serviceCtxtId);
				}
				priceEnq.setServiceContextId(this._serviceCtxtId);

				// Copy Username
				if(logger.isDebugEnabled()) {
					logger.debug("User name is: " + this._userName);
				}
				if(this._userName != null) {
					priceEnq.setUserName(this._userName);
					priceEnq.setIsUserNamePresent(true);
				} else {
					priceEnq.setIsUserNamePresent(false);
				}

				// Copy custom AVPs
				if(this.getCustomAVPMap() != null) {
					priceEnq.setCustomAvpInfo(CustomAVPCopier.createAVPInfo(this.getCustomAVPMap()));
					priceEnq.setIsCustomAvpPresent(true);
				}

				this._stackReq = priceEnq;
			break;

			default:
				throw new IllegalArgumentException("Unknown request type : " + this._reqType);
		}

		// Make this request object immutable
		this._readonly = true;

		return this._stackReq;
	}

	public AAADestinationInfo getDestinationInfo() {
		AAADestinationInfo destInfo = new AAADestinationInfo();
		if(this._destHost != null) {
			destInfo.destHost = this._destHost.get();
		}

		if(this._destRealm != null) {
			destInfo.destRealm = this._destRealm.get();
		}

		return destInfo;
	}

	private void checkReadOnly() throws RoResourceException {
		if(this._readonly) {
			throw new RoResourceException("Cannot modify this message");
		}
	}

	public void isAlreadyReplicated ( boolean isReplicated ) {
		this.isReplicated = isReplicated ;
	}
	
	/*
	/////////////// Replicable interface methods ////////////////////

	public String getReplicableId() {
		logger.debug("replicable ID is" + replicableId);
		return replicableId ;
	}

	public void setReplicableId(String replicableId) {
		logger.debug("setReplicableId called .");
		this.replicableId = replicableId ;
	}

	public boolean isReadyForReplication() {
		logger.debug("isReadyForReplication returns "+isReadyForReplication);
		return isReadyForReplication ;
	}
	
	private void setReadyForReplication( boolean isReady) {
		logger.debug("setReadyForReplication called with flag " + isReady);
		this.isReadyForReplication = isReady ;
	}
	
	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		//TODO NOOP
	}

	public void writeIncremental(ObjectOutput out)throws IOException {
		// TODO NOOP
	}            	
	
	public void activate(ReplicationSet parent) {
		// TODO 
	}
	
	public boolean isModified() {
		// TODO NOOP
		return false;
	}
		
	public boolean isNew() {
		// TODO 
		return false ;
	}

	public void replicationCompleted() {
		// TODO NOOP
	}

	////////// Replicable interface methods ends //////////////////////////

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO 
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO 
	}

	private String replicableId = null;

	private boolean isReadyForReplication = false;	
	*/
	private boolean isReplicated = false ;
}
