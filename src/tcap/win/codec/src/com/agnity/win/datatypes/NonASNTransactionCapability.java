package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.enumdata.BusyDetectionEnum;
import com.agnity.win.enumdata.SubscriberPINInterceptEnum;
import com.agnity.win.enumdata.RemoteUserInteractionEnum;
import com.agnity.win.enumdata.TerminationListEnum;
import com.agnity.win.enumdata.AnnouncementsEnum;
import com.agnity.win.enumdata.ProfileEnum;
import com.agnity.win.enumdata.MultipleTerminationsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for TransactionCapability
 * as per definition given in TIA-EIA-41-D, section 6.5.2.160.
 *  @author Supriya Jain
 */

public class NonASNTransactionCapability {

	private static Logger logger = Logger.getLogger(NonASNTransactionCapability.class);

	/*
	 * Can detect Busy condition or not
	 */
	BusyDetectionEnum busyDetection;

	/*
	 * Can Support LOCAL SPINI OPERATION or not 
	 */
	SubscriberPINInterceptEnum subscrbrPinIntrcpt;
	
	 /*
	  * System Capable of Interacting with user or not
	  */
	RemoteUserInteractionEnum remoteUsrInteract;
	 /*
	  * System Capable of Supporting termination List parameter or not
	  */
	TerminationListEnum terminationList;
	 /*
	  * System Capable of Supporting announcement List parameter or not
	  */
	AnnouncementsEnum announcements;
	 /*
	  * System Capable of Supporting IS41C profile parameter or not
	  */
	ProfileEnum profile;
	 /*
	  * If System Can accept termination, else number of call legs supported 
	  */
	MultipleTerminationsEnum multiTerminations;


	/**
	 * This function will decode TransactionCapability as per specification TIA-EIA-41-D
	 * section 6.5.2.160
	 * @param data
	 * @return decoded TransactionCapability
	 * @throws InvalidInputException
	 */
	public static NonASNTransactionCapability decodeTransactionCapability(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeTransactionCapability: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null) {
			logger.error("decodeTransactionCapability: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		
		// Check if number of bytes is minimum 2
				if (data.length < 2) {
					logger.error("decodeTransactionCapability: InvalidInputException(data is incomplete)");
					throw new InvalidInputException("data is incomplete");
				}
				
		NonASNTransactionCapability transactionCapability = new NonASNTransactionCapability();

		transactionCapability.profile = ProfileEnum.fromInt((int) (data[0]& 0x01));
		transactionCapability.busyDetection = BusyDetectionEnum.fromInt((int) (data[0]>>1 & 0x01));
		transactionCapability.announcements = AnnouncementsEnum
				.fromInt((int) ((data[0] >> 2) & 0x01));
		transactionCapability.remoteUsrInteract = RemoteUserInteractionEnum
				.fromInt((int) ((data[0] >> 3) & 0x01));
		transactionCapability.subscrbrPinIntrcpt = SubscriberPINInterceptEnum
				.fromInt((int) ((data[0] >> 4) & 0x01));
		transactionCapability.multiTerminations = MultipleTerminationsEnum.fromInt((int) (data[1] & 0x0F));
		transactionCapability.terminationList = TerminationListEnum.fromInt((int) ((data[1] >> 4) & 0x01));

		if (logger.isDebugEnabled())
			logger.debug("decodeTransactionCapability: Output<--" + transactionCapability.toString());
		logger.info("decodeTransactionCapability");

		return transactionCapability;
	}


	/**
	 * This function will encode TransactionCapability as per specification TIA-EIA-41-D
	 * section 6.5.2.160

	 * @param pr
	 *            - CAN SUPPORT IS41C profile PARAMETERS or Not
	 * @param bd
	 *            - DETECT BUSY CONDITION OR NOT
	 * @param ann
	 *            - CAN SUPPORT ANNOUNCEMENT LIST PARAMETER OR NOT
	 * @param rei
	 *            - Capable of Interacting with user OR NOT
	 * @param spi
	 *            - CAN SUPPORT LOCAL SPINI OPERATION OR NOT
	 * @param mt
	 *            - THE NUMBER OF CALL LEGS SUPPORTED
	 * @param tl
	 *            - CAN SUPPORT TERMINATION LIST PARAMETER OR NOT
	 * @return byte[] of encoded TransactionCapability
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTransactionCapability(ProfileEnum pr, BusyDetectionEnum bd,
			AnnouncementsEnum ann, RemoteUserInteractionEnum rei,SubscriberPINInterceptEnum spi,
			MultipleTerminationsEnum mt, TerminationListEnum tl) throws InvalidInputException {

		logger.info("encodeTransactionCapability");

		int profileBit = 0;
		if (pr == null) {
			logger.error("encodeTransactionCapability: Profile bit not present");
			throw new InvalidInputException(
					"Profile bit not present");
		} else {
			profileBit = pr.getCode();
		}

		byte[] myParams = new byte[2];

		int busyBit = 0;
		if (bd == null) {
			logger.error("encodeTransactionCapability: Busy bit not present");
			throw new InvalidInputException(
					"Busy bit not present");
		}
		else
		{
			busyBit =bd.getCode();
		}

		int announcementBit = 0;

		if (ann == null) {
			logger.error("encodeTransactionCapability: Announcement bit not present");
			throw new InvalidInputException(
					"Announcement bit not present");
		} else {
			announcementBit = ann.getCode() ;
		}

		int remUsrBit = 0;
		if (rei == null) {
			logger.error("encodeTransactionCapability: Remote User bit not present");
			throw new InvalidInputException(
					"Remote User bit not present");
		} else {
			remUsrBit =rei.getCode();
		}
		int subPinBit = 0;

		if (spi == null) {
			logger.error("encodeTransactionCapability: Subscriber Pin bit not present");
			throw new InvalidInputException(
					"Subscriber Pinr bit not present");
		} else {
			subPinBit = spi.getCode();
		}
		
		int mulTermBit = 0;

		if (mt == null) {
			logger.error("encodeTransactionCapability: Multiple Termination not present");
			throw new InvalidInputException(
					"Multiple Termination bit not present");
		} else {
			mulTermBit =mt.getCode();
		}

		int terListBit = 0;
		if (tl == null) {
			logger.error("encodeTransactionCapability: Termination List bit not present");
			throw new InvalidInputException(
					" Termination List bit not present");
		} else {
			terListBit = tl.getCode();
		}

		// Start Announcements
		myParams[0] = (byte) ((profileBit)|(busyBit<<1)|(announcementBit<<2)|(remUsrBit<<3)|(subPinBit<<4));
		myParams[1] = (byte) ((mulTermBit)|(terListBit<<4) );

		if (logger.isDebugEnabled())
			logger.debug("encodeTransactionCapability: Encoded TransactionCapability: "
					+ Util.formatBytes(myParams));
		logger.info("encodeTransactionCapability:Exit");

		return myParams;
	}
	
	/**
	 * This function will encode Non ASN TransactionCapabilityType to ASN TransactionCapabilityType object
	 * @param NonASNTransactionCapability
	 * @return TransactionCapabilityType
	 * @throws InvalidInputException
	 */
	public static TransactionCapability encodeTransactionCapability(NonASNTransactionCapability nonASNTransactionCapability)
			throws InvalidInputException {
		
		logger.info("Before encodeTransactionCapability : nonASN to ASN");
		TransactionCapability transactionCapability = new TransactionCapability();
		transactionCapability.setValue(encodeTransactionCapability(nonASNTransactionCapability.getProfile(),nonASNTransactionCapability.getBusyDetection(),
				nonASNTransactionCapability.getAnnouncements(),nonASNTransactionCapability.getRemoteUserInteraction(),
				nonASNTransactionCapability.getSubscriberPINIntercept(),nonASNTransactionCapability.getMultipleTerminations(),
				nonASNTransactionCapability.getTerminationList()));
		logger.info("After encodeTransactionCapabilityType : nonASN to ASN");
		return transactionCapability;
	}

	public ProfileEnum getProfile() {
		return profile;
	}

	public void setProfile(ProfileEnum profileEnum) {
		this.profile = profileEnum;
	}

	public BusyDetectionEnum  getBusyDetection() {
		return busyDetection;
	}

	public void setBusyDetection(BusyDetectionEnum busyDetectionEnum) {
		this.busyDetection = busyDetectionEnum;
	}

	public AnnouncementsEnum  getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(AnnouncementsEnum announcementsEnum) {
		this.announcements = announcementsEnum;
	}

	public RemoteUserInteractionEnum getRemoteUserInteraction() {
		return remoteUsrInteract;
	}

	public void setRemoteUserInteraction(RemoteUserInteractionEnum remoteUserInteractionEnum) {
		this.remoteUsrInteract = remoteUserInteractionEnum;
	}

	public SubscriberPINInterceptEnum  getSubscriberPINIntercept() {
		return subscrbrPinIntrcpt;
	}

	public void setSubscriberPINIntercept(SubscriberPINInterceptEnum subscriberPINInterceptEnum) {
		this.subscrbrPinIntrcpt = subscriberPINInterceptEnum;
	}

	public MultipleTerminationsEnum  getMultipleTerminations() {
		return multiTerminations;
	}

	public void setMultipleTerminations(
			MultipleTerminationsEnum multipleTerminationsEnum) {
		this.multiTerminations = multipleTerminationsEnum;
	}

	public TerminationListEnum  getTerminationList() {
		return terminationList;
	}

	public void setTerminationList(TerminationListEnum terminationListEnum) {
		this.terminationList = terminationListEnum;
	}

	public String toString() {
		String obj = "profile: " + profile + " ,busyDetection: "
				+ busyDetection + " announcements: " + announcements + " remoteUsrInteract: "
				+ remoteUsrInteract + " ,subscrbrPinIntrcpt: " + subscrbrPinIntrcpt
				+ " multiTerminations:" + multiTerminations + " terminationList:" + terminationList;

		return obj;
	}
}
