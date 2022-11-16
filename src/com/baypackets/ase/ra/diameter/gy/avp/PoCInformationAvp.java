package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.PoCEventTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.PoCServerRoleEnum;
import com.baypackets.ase.ra.diameter.gy.enums.PoCSessionInitiationTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.PoCSessionTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpPoCInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpParticipantGroup;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTalkBurstExchange;
import com.traffix.openblox.diameter.gy.generated.enums.EnumPoCEventType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumPoCServerRole;
import com.traffix.openblox.diameter.gy.generated.enums.EnumPoCSessionType;

public class PoCInformationAvp extends AvpDiameterGrouped
{

	private static Logger logger = Logger.getLogger(PoCInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpPoCInformation stackObj;

	public PoCInformationAvp(AvpPoCInformation stkObj){
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
	 *  Adding ChargedParty AVP of type UTF8String to the message.
	 */
	public ChargedPartyAvp addChargedParty(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addChargedParty()");
			}
			return new ChargedPartyAvp(stackObj.addChargedParty(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addChargedParty",e);
		}
	}

	/**
	 *  Adding ParticipantGroup AVP of type Grouped to the message.
	 */
	public ParticipantGroupAvp addGroupedParticipantGroup() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedParticipantGroup()");
			}
			return new ParticipantGroupAvp(stackObj.addGroupedParticipantGroup());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedParticipantGroup",e);
		}
	}

	/**
	 *  Adding PoCUserRole AVP of type Grouped to the message.
	 */
	public PoCUserRoleAvp addGroupedPoCUserRole() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedPoCUserRole()");
			}
			return new PoCUserRoleAvp(stackObj.addGroupedPoCUserRole());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedPoCUserRole",e);
		}
	}

	/**
	 *  Adding TalkBurstExchange AVP of type Grouped to the message.
	 */
	public TalkBurstExchangeAvp addGroupedTalkBurstExchange() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTalkBurstExchange()");
			}
			return new TalkBurstExchangeAvp(stackObj.addGroupedTalkBurstExchange());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTalkBurstExchange",e);
		}
	}

	/**
	 *  Adding NumberOfParticipants AVP of type Unsigned32 to the message.
	 */
	public NumberOfParticipantsAvp addNumberOfParticipants(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfParticipants()");
			}
			return new NumberOfParticipantsAvp(stackObj.addNumberOfParticipants(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfParticipants",e);
		}
	}

	/**
	 *  Adding ParticipantsInvolved AVP of type UTF8String to the message.
	 */
	public ParticipantsInvolvedAvp addParticipantsInvolved(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addParticipantsInvolved()");
			}
			return new ParticipantsInvolvedAvp(stackObj.addParticipantsInvolved(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addParticipantsInvolved",e);
		}
	}

	/**
	 *  Adding PoCControllingAddress AVP of type UTF8String to the message.
	 */
	public PoCControllingAddressAvp addPoCControllingAddress(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCControllingAddress()");
			}
			return new PoCControllingAddressAvp(stackObj.addPoCControllingAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCControllingAddress",e);
		}
	}

	/**
	 *  Adding PoCEventType AVP of type Enumerated to the message.
	 */
	public PoCEventTypeAvp addPoCEventType(EnumPoCEventType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCEventType()");
			}
			return new PoCEventTypeAvp(stackObj.addPoCEventType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCEventType",e);
		}
	}

	/**
	 *  Adding PoCGroupName AVP of type UTF8String to the message.
	 */
	public PoCGroupNameAvp addPoCGroupName(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCGroupName()");
			}
			return new PoCGroupNameAvp(stackObj.addPoCGroupName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCGroupName",e);
		}
	}

	/**
	 *  Adding PoCServerRole AVP of type Enumerated to the message.
	 */
	public PoCServerRoleAvp addPoCServerRole(EnumPoCServerRole value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCServerRole()");
			}
			return new PoCServerRoleAvp(stackObj.addPoCServerRole(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCServerRole",e);
		}
	}

	/**
	 *  Adding PoCSessionId AVP of type UTF8String to the message.
	 */
	public PoCSessionIdAvp addPoCSessionId(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCSessionId()");
			}
			return new PoCSessionIdAvp(stackObj.addPoCSessionId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCSessionId",e);
		}
	}

	/**
	 *  Adding PoCSessionInitiationType AVP of type Enumerated to the message.
	 */
	public PoCSessionInitiationTypeAvp addPoCSessionInitiationType(PoCSessionInitiationTypeEnum value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCSessionInitiationType()");
			}
			return new PoCSessionInitiationTypeAvp(stackObj.addPoCSessionInitiationType(PoCSessionInitiationTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCSessionInitiationType",e);
		}
	}

	/**
	 *  Adding PoCSessionType AVP of type Enumerated to the message.
	 */
	public PoCSessionTypeAvp addPoCSessionType(EnumPoCSessionType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCSessionType()");
			}
			return new PoCSessionTypeAvp(stackObj.addPoCSessionType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCSessionType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ChargedParty AVPs.
	 */
	public java.lang.String getChargedParty() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChargedParty()");
			}
			return stackObj.getChargedParty();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChargedParty",e);
		}
	}

	/**
	 *  Retrieves Enum of PoCEventType.
	 */
	public PoCEventTypeEnum getEnumPoCEventType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPoCEventType()");
			}
			return PoCEventTypeEnum.getContainerObj(stackObj.getEnumPoCEventType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPoCEventType",e);
		}
	}

	/**
	 *  Retrieves Enum of PoCServerRole type.
	 */
	public PoCServerRoleEnum getEnumPoCServerRole() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPoCServerRole()");
			}
			return PoCServerRoleEnum.getContainerObj(stackObj.getEnumPoCServerRole());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPoCServerRole",e);
		}
	}

	/**
	 *  Retrieves Enum of PoCSessionInitiationType.
	 */
	public PoCSessionInitiationTypeEnum getEnumPoCSessionInitiationType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPoCSessionInitiationType()");
			}
			return PoCSessionInitiationTypeEnum.getContainerObj(stackObj.getEnumPoCSessionInitiationType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPoCSessionInitiationType",e);
		}
	}

	/**
	 *  Retrieves Enum of PoCSessionType.
	 */
	public PoCSessionTypeEnum getEnumPoCSessionType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPoCSessionType()");
			}
			return PoCSessionTypeEnum.getContainerObj(stackObj.getEnumPoCSessionType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPoCSessionType",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from ParticipantGroup AVPs.
	 */
	public ParticipantGroupAvp[] getGroupedParticipantGroups() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedParticipantGroups()");
			}
			AvpParticipantGroup[] stackAv= stackObj.getGroupedParticipantGroups();
			ParticipantGroupAvp[] contAvp= new ParticipantGroupAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new ParticipantGroupAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedParticipantGroups",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from PoCUserRole AVPs.
	 */
	public PoCUserRoleAvp getGroupedPoCUserRole() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedPoCUserRole()");
			}
			return new PoCUserRoleAvp(stackObj.getGroupedPoCUserRole());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedPoCUserRole",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from TalkBurstExchange AVPs.
	 */
	public TalkBurstExchangeAvp[] getGroupedTalkBurstExchanges() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTalkBurstExchanges()");
			}
			AvpTalkBurstExchange[] stackAv= stackObj.getGroupedTalkBurstExchanges();
			TalkBurstExchangeAvp[] contAvp= new TalkBurstExchangeAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new TalkBurstExchangeAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTalkBurstExchanges",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfParticipants AVPs.
	 */
	public long getNumberOfParticipants() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfParticipants()");
			}
			return stackObj.getNumberOfParticipants();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfParticipants",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from ParticipantsInvolved AVPs.
	 */
	public java.lang.String[] getParticipantsInvolveds() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getParticipantsInvolveds()");
			}
			return stackObj.getParticipantsInvolveds();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getParticipantsInvolveds",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from PoCControllingAddress AVPs.
	 */
	public java.lang.String getPoCControllingAddress() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCControllingAddress()");
			}
			return stackObj.getPoCControllingAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCControllingAddress",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PoCEventType AVPs.
	 */
	public int getPoCEventType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCEventType()");
			}
			return stackObj.getPoCEventType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCEventType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from PoCGroupName AVPs.
	 */
	public java.lang.String getPoCGroupName() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCGroupName()");
			}
			return stackObj.getPoCGroupName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCGroupName",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PoCServerRole AVPs.
	 */
	public int getPoCServerRole() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCServerRole()");
			}
			return stackObj.getPoCServerRole();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCServerRole",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from PoCSessionId AVPs.
	 */
	public java.lang.String getPoCSessionId() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCSessionId()");
			}
			return stackObj.getPoCSessionId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCSessionId",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PoCSessionInitiationType AVPs.
	 */
	public int getPoCSessionInitiationType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCSessionInitiationType()");
			}
			return stackObj.getPoCSessionInitiationType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCSessionInitiationType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PoCSessionType AVPs.
	 */
	public int getPoCSessionType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCSessionType()");
			}
			return stackObj.getPoCSessionType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCSessionType",e);
		}
	}


}