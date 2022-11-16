package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpServiceInformation;

public class ServiceInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ServiceInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpServiceInformation stackObj;

	public ServiceInformationAvp(AvpServiceInformation stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	public int getCode() {
		return stackObj.getCode();
	}

	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	public String getName() {
		return stackObj.getName();
	}

	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	public long getVendorId() {
		return stackObj.getVendorId();
	}

	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}




	/**
	 *  Adding AoCInformation AVP of type Grouped to the message.
	 */
	public AoCInformationAvp addGroupedAoCInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAoCInformation()");
			}
			return new AoCInformationAvp(stackObj.addGroupedAoCInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedAoCInformation",e);
		}
	}

	/**
	 *  Adding DCDInformation AVP of type Grouped to the message.
	 */
	public DCDInformationAvp addGroupedDCDInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedDCDInformation()");
			}
			return new DCDInformationAvp(stackObj.addGroupedDCDInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedDCDInformation",e);
		}
	}

	/**
	 *  Adding IMSInformation AVP of type Grouped to the message.
	 */
	public IMSInformationAvp addGroupedIMSInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedIMSInformation()");
			}
			return new IMSInformationAvp(stackObj.addGroupedIMSInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedIMSInformation",e);
		}
	}

	/**
	 *  Adding LCSInformation AVP of type Grouped to the message.
	 */
	public LCSInformationAvp addGroupedLCSInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedLCSInformation()");
			}
			return new LCSInformationAvp(stackObj.addGroupedLCSInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedLCSInformation",e);
		}
	}

	// TODO in future
//	/**
//	 *  Adding MBMSInformation AVP of type Grouped to the message.
//	 */
//	public MBMSInformationAvp addGroupedMBMSInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedMBMSInformation()");
//			}
//			return new MBMSInformationAvp(stackObj.addGroupedMBMSInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedMBMSInformation",e);
//		}
//	}

	/**
	 *  Adding MMSInformation AVP of type Grouped to the message.
	 */
	public MMSInformationAvp addGroupedMMSInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMMSInformation()");
			}
			return new MMSInformationAvp(stackObj.addGroupedMMSInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedMMSInformation",e);
		}
	}

	// TODO
//	/**
//	 *  Adding MMTelInformation AVP of type Grouped to the message.
//	 */
//	public MMTelInformationAvp addGroupedMMTelInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedMMTelInformation()");
//			}
//			return new MMTelInformationAvp(stackObj.addGroupedMMTelInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedMMTelInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding PoCInformation AVP of type Grouped to the message.
//	 */
//	public PoCInformationAvp addGroupedPoCInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedPoCInformation()");
//			}
//			return new PoCInformationAvp(stackObj.addGroupedPoCInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedPoCInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding PSInformation AVP of type Grouped to the message.
//	 */
//	public PSInformationAvp addGroupedPSInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedPSInformation()");
//			}
//			return new PSInformationAvp(stackObj.addGroupedPSInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedPSInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding ServiceGenericInformation AVP of type Grouped to the message.
//	 */
//	public ServiceGenericInformationAvp addGroupedServiceGenericInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedServiceGenericInformation()");
//			}
//			return new ServiceGenericInformationAvp(stackObj.addGroupedServiceGenericInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedServiceGenericInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding SMSInformation AVP of type Grouped to the message.
//	 */
//	public SMSInformationAvp addGroupedSMSInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedSMSInformation()");
//			}
//			return new SMSInformationAvp(stackObj.addGroupedSMSInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedSMSInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding SubscriptionId AVP of type Grouped to the message.
//	 */
//	public SubscriptionIdAvp addGroupedSubscriptionId() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedSubscriptionId()");
//			}
//			return new SubscriptionIdAvp(stackObj.addGroupedSubscriptionId());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedSubscriptionId",e);
//		}
//	}
//
//	/**
//	 *  Adding WLANInformation AVP of type Grouped to the message.
//	 */
//	public WLANInformationAvp addGroupedWLANInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedWLANInformation()");
//			}
//			return new WLANInformationAvp(stackObj.addGroupedWLANInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedWLANInformation",e);
//		}
//	}
//
//	/**
//	 *  Adding IMInformation AVP of type UTF8String to the message.
//	 */
//	public IMInformationAvp addIMInformation(java.lang.String value) throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addIMInformation()");
//			}
//			return new IMInformationAvp(stackObj.addIMInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addIMInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from AoCInformation AVPs.
	 */
	public AoCInformationAvp getGroupedAoCInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAoCInformation()");
			}
			return new AoCInformationAvp(stackObj.getGroupedAoCInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedAoCInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from DCDInformation AVPs.
	 */
	public DCDInformationAvp getGroupedDCDInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedDCDInformation()");
			}
			return new DCDInformationAvp(stackObj.getGroupedDCDInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedDCDInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from IMSInformation AVPs.
	 */
	public IMSInformationAvp getGroupedIMSInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedIMSInformation()");
			}
			return new IMSInformationAvp(stackObj.getGroupedIMSInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedIMSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from LCSInformation AVPs.
	 */
	public LCSInformationAvp getGroupedLCSInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedLCSInformation()");
			}
			return new LCSInformationAvp(stackObj.getGroupedLCSInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedLCSInformation",e);
		}
	}

	// TODO
//	/**
//	 *  Retrieving a single Grouped value from MBMSInformation AVPs.
//	 */
//	public MBMSInformationAvp getGroupedMBMSInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedMBMSInformation()");
//			}
//			return new MBMSInformationAvp(stackObj.getGroupedMBMSInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedMBMSInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from MMSInformation AVPs.
	 */
	public MMSInformationAvp getGroupedMMSInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMMSInformation()");
			}
			return new MMSInformationAvp(stackObj.getGroupedMMSInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedMMSInformation",e);
		}
	}

	// TODO
//	/**
//	 *  Retrieving a single Grouped value from MMTelInformation AVPs.
//	 */
//	public MMTelInformationAvp getGroupedMMTelInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedMMTelInformation()");
//			}
//			return new MMTelInformationAvp(stackObj.getGroupedMMTelInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedMMTelInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from PoCInformation AVPs.
//	 */
//	public PoCInformationAvp getGroupedPoCInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedPoCInformation()");
//			}
//			return new PoCInformationAvp(stackObj.getGroupedPoCInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedPoCInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from PSInformation AVPs.
//	 */
//	public PSInformationAvp getGroupedPSInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedPSInformation()");
//			}
//			return new PSInformationAvp(stackObj.getGroupedPSInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedPSInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from ServiceGenericInformation AVPs.
//	 */
//	public ServiceGenericInformationAvp getGroupedServiceGenericInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedServiceGenericInformation()");
//			}
//			return new ServiceGenericInformationAvp(stackObj.getGroupedServiceGenericInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedServiceGenericInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from SMSInformation AVPs.
//	 */
//	public SMSInformationAvp getGroupedSMSInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedSMSInformation()");
//			}
//			return new SMSInformationAvp(stackObj.getGroupedSMSInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedSMSInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving multiple Grouped values from SubscriptionId AVPs.
//	 */
//	public SubscriptionId[]Avp getGroupedSubscriptionIds() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedSubscriptionIds()");
//			}
//			return new SubscriptionId[]Avp(stackObj.getGroupedSubscriptionIds());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedSubscriptionIds",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from WLANInformation AVPs.
//	 */
//	public WLANInformationAvp getGroupedWLANInformation() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedWLANInformation()");
//			}
//			return new WLANInformationAvp(stackObj.getGroupedWLANInformation());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedWLANInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single UTF8String value from IMInformation AVPs.
	 */
	public java.lang.String getIMInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getIMInformation()");
			}
			return stackObj.getIMInformation();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getIMInformation",e);
		}
	}




}