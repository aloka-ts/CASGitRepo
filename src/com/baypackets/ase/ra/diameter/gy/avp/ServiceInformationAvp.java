package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSubscriptionId;

public class ServiceInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ServiceInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpServiceInformation stackObj;

	public ServiceInformationAvp(AvpServiceInformation stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	/**
	 * This method returns the standard code for this AVP.
	 */
	public int getCode() {
		return stackObj.getCode();
	}

	/**
	 * The standard rule for the M (mandatory) AVP header flag
	 */
	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	/**
	 * This method returns the name of this AVP.
	 */
	public String getName() {
		return stackObj.getName();
	}

	/**
	 * The standard rule for the P (end-to-end encryption) AVP header flag
	 */
	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	/**
	 * This method returns the vendor ID associated with this AVP.
	 */
	public long getVendorId() {
		return stackObj.getVendorId();
	}

	/**
	 * The standard rule for the V (vendor) AVP header flag
	 */
	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}

	/**
	 *  Adding AoCInformation AVP of type Grouped to the message.
	 */
//	public AoCInformationAvp addGroupedAoCInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedAoCInformation()");
//			}
//			return new AoCInformationAvp(stackObj.addGroupedAoCInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedAoCInformation",e);
//		}
//	}

	/**
	 *  Adding DCDInformation AVP of type Grouped to the message.
	 */
//	public DCDInformationAvp addGroupedDCDInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedDCDInformation()");
//			}
//			return new DCDInformationAvp(stackObj.addGroupedDCDInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedDCDInformation",e);
//		}
//	}

	/**
	 *  Adding IMSInformation AVP of type Grouped to the message.
	 */
	public IMSInformationAvp addGroupedIMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedIMSInformation()");
			}
			return new IMSInformationAvp(stackObj.addGroupedIMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedIMSInformation",e);
		}
	}

	/**
	 *  Adding LCSInformation AVP of type Grouped to the message.
	 */
	public LCSInformationAvp addGroupedLCSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedLCSInformation()");
			}
			return new LCSInformationAvp(stackObj.addGroupedLCSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedLCSInformation",e);
		}
	}

	/**
	 *  Adding MBMSInformation AVP of type Grouped to the message.
	 */
	public MBMSInformationAvp addGroupedMBMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMBMSInformation()");
			}
			return new MBMSInformationAvp(stackObj.addGroupedMBMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedMBMSInformation",e);
		}
	}

	/**
	 *  Adding MMSInformation AVP of type Grouped to the message.
	 */
	public MMSInformationAvp addGroupedMMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMMSInformation()");
			}
			return new MMSInformationAvp(stackObj.addGroupedMMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedMMSInformation",e);
		}
	}

	/**
	 *  Adding MMTelInformation AVP of type Grouped to the message.
	 */
//	public MMTelInformationAvp addGroupedMMTelInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedMMTelInformation()");
//			}
//			return new MMTelInformationAvp(stackObj.addGroupedMMTelInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedMMTelInformation",e);
//		}
//	}

	/**
	 *  Adding PoCInformation AVP of type Grouped to the message.
	 */
	public PoCInformationAvp addGroupedPoCInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedPoCInformation()");
			}
			return new PoCInformationAvp(stackObj.addGroupedPoCInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedPoCInformation",e);
		}
	}

	/**
	 *  Adding PSInformation AVP of type Grouped to the message.
	 */
	public PSInformationAvp addGroupedPSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedPSInformation()");
			}
			return new PSInformationAvp(stackObj.addGroupedPSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedPSInformation",e);
		}
	}

	/**
	 *  Adding ServiceGenericInformation AVP of type Grouped to the message.
	 */
	public ServiceGenericInformationAvp addGroupedServiceGenericInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServiceGenericInformation()");
			}
			return new ServiceGenericInformationAvp(stackObj.addGroupedServiceGenericInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedServiceGenericInformation",e);
		}
	}

	/**
	 *  Adding SMSInformation AVP of type Grouped to the message.
	 */
	public SMSInformationAvp addGroupedSMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSMSInformation()");
			}
			return new SMSInformationAvp(stackObj.addGroupedSMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSMSInformation",e);
		}
	}

	/**
	 *  Adding SubscriptionId AVP of type Grouped to the message.
	 */
	public SubscriptionIdAvp addGroupedSubscriptionId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSubscriptionId()");
			}
			return new SubscriptionIdAvp(stackObj.addGroupedSubscriptionId());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSubscriptionId",e);
		}
	}

	/**
	 *  Adding WLANInformation AVP of type Grouped to the message.
	 */
	public WLANInformationAvp addGroupedWLANInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedWLANInformation()");
			}
			return new WLANInformationAvp(stackObj.addGroupedWLANInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedWLANInformation",e);
		}
	}

	/**
	 *  Adding IMInformation AVP of type UTF8String to the message.
	 */
//	public IMInformationAvp addIMInformation(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addIMInformation()");
//			}
//			return new IMInformationAvp(stackObj.addIMInformation(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addIMInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from AoCInformation AVPs.
	 */
//	public AoCInformationAvp getGroupedAoCInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedAoCInformation()");
//			}
//			return new AoCInformationAvp(stackObj.getGroupedAoCInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedAoCInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from DCDInformation AVPs.
	 */
//	public DCDInformationAvp getGroupedDCDInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedDCDInformation()");
//			}
//			return new DCDInformationAvp(stackObj.getGroupedDCDInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedDCDInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from IMSInformation AVPs.
	 */
	public IMSInformationAvp getGroupedIMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedIMSInformation()");
			}
			return new IMSInformationAvp(stackObj.getGroupedIMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedIMSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from LCSInformation AVPs.
	 */
	public LCSInformationAvp getGroupedLCSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedLCSInformation()");
			}
			return new LCSInformationAvp(stackObj.getGroupedLCSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedLCSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from MBMSInformation AVPs.
	 */
	public MBMSInformationAvp getGroupedMBMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMBMSInformation()");
			}
			return new MBMSInformationAvp(stackObj.getGroupedMBMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedMBMSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from MMSInformation AVPs.
	 */
	public MMSInformationAvp getGroupedMMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMMSInformation()");
			}
			return new MMSInformationAvp(stackObj.getGroupedMMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedMMSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from MMTelInformation AVPs.
	 */
//	public MMTelInformationAvp getGroupedMMTelInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedMMTelInformation()");
//			}
//			return new MMTelInformationAvp(stackObj.getGroupedMMTelInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedMMTelInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from PoCInformation AVPs.
	 */
	public PoCInformationAvp getGroupedPoCInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedPoCInformation()");
			}
			return new PoCInformationAvp(stackObj.getGroupedPoCInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedPoCInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from PSInformation AVPs.
	 */
	public PSInformationAvp getGroupedPSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedPSInformation()");
			}
			return new PSInformationAvp(stackObj.getGroupedPSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedPSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from ServiceGenericInformation AVPs.
	 */
	public ServiceGenericInformationAvp getGroupedServiceGenericInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceGenericInformation()");
			}
			return new ServiceGenericInformationAvp(stackObj.getGroupedServiceGenericInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceGenericInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from SMSInformation AVPs.
	 */
	public SMSInformationAvp getGroupedSMSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedSMSInformation()");
			}
			return new SMSInformationAvp(stackObj.getGroupedSMSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedSMSInformation",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from SubscriptionId AVPs.
	 */
//	public SubscriptionIdAvp[] getGroupedSubscriptionIds( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedSubscriptionIds()");
//			}
//			AvpSubscriptionId[] stackAv= stackObj.getGroupedSubscriptionIds();
//			SubscriptionIdAvp[] contAvp= new SubscriptionIdAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new SubscriptionIdAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedSubscriptionIds",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from WLANInformation AVPs.
	 */
	public WLANInformationAvp getGroupedWLANInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedWLANInformation()");
			}
			return new WLANInformationAvp(stackObj.getGroupedWLANInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedWLANInformation",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from IMInformation AVPs.
	 */
//	public java.lang.String getIMInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getIMInformation()");
//			}
//			return stackObj.getIMInformation();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getIMInformation",e);
//		}
//	}


}