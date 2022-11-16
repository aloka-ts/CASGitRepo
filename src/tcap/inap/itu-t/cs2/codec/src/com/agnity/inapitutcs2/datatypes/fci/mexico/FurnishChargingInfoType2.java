/****
Copyright (c) 2015 Agnity, Inc. All rights reserved.


This is proprietary source code of Agnity, Inc.


Agnity, Inc. retains all intellectual property rights associated 
with this source code. Use is subject to license terms.

This source code contains trade secrets owned by Agnity, Inc.
Confidentiality of this computer program must be maintained at
 all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.agnity.inapitutcs2.datatypes.fci.mexico;

import java.io.Serializable;
import java.nio.charset.*;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import com.agnity.inapitutcs2.datatypes.AddressSignal;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;

/**
 * @author rarya
 * 
 * This method contains FCI definition for Axtel. 
 * For the FCI information development, the specification shall be according to the next information:
 * Tag Hex |	Field Name	 | Availability  |Type	              | Description	Max length
 * ----------------------------------------------------------------------------------------------------
 * 2A	   | Correlation ID	 |  Mandatory	 |  Numeric	          | 0 to 9223372036854775807: encoded as BCD 
 *         |                 |               |                    | 10 bytes + header (2A) 12 Bytes
 *-------------------------------------------------------------------------------------------------------
 * 2B	   | Corp ID	     |  Mandatory	 | Digit string	      | 1 to 28 positions: encoded as BCD	
 *         |                 |               |                    | 14 bytes + header (2B)16 Bytes
 *-------------------------------------------------------------------------------------------------------
 * 2C	   | Account number	 |  Mandatory	 | Alphanumeric String|	1 to 28 positions:  Byte Encoded.	
 *         |                 |               |                    | 28 bytes + header (2C)30 Bytes
 *-------------------------------------------------------------------------------------------------------
 * 2D	   | VPN ID	         |  Mandatory	 | Alphanumeric String|	1 to 28 positions: Byte Encoded.	
 *         |                 |               |                    | 28 bytes + header (2D) 30 Bytes
 *-------------------------------------------------------------------------------------------------------
 * 2E	   | IDCO	         |  Optional	 | Digit string	      | 4 to 10 positions: encoded as BCD	
 *         |                 |               |                    | 5 bytes + header (2E) 7 Bytes
 *--------------------------------------------------------------------------------------------------------
 * 2F	   | Calling Card	 |  Optional	 | Digit string	      | 2 to 10 positions: encoded as BCD	
 *                                                                | 5 bytes + header (2F)7 Bytes
 *                                                          
 * Note1: The Field values will depend on what is defined for the customer during provisioning.
 * Note2: Optional fields sent with zero length if no data available.
 * 
 */
/**
 * @author rarya
 *
 */
/**
 * @author rarya
 *
 */
public class FurnishChargingInfoType2 extends FurnishChargingMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(FurnishChargingInfoType2.class);

	private String correlationID;
	private String serviceProviderId;
	private String enterpriseId;
	private String vpnId; 
	private String idco;
	private String callingCard;

	// Field max length allowed
	final int CORRELATION_ID_MAX_LENGTH      = 20;
	final int SERVICE_PROVIDER_ID_MAX_LENGTH = 28;
	final int ENTERPRISE_ID_MAX_LENGTH       = 28;
	final int VPN_ID_MAX_LENGTH              = 28;
	final int IDCO_MAX_LENGTH                = 10;
	final int CALLING_CARD_MAX_LENGTH        = 10;
	final int HDR_LENGTH					 = 2; 
	final int FCI_MSG_PREAMBLE_LEN           = 4; 

	// Tag values
	final byte TAG_FCI_HDR             = (byte) 0xA0;
	final byte TAG_FCI_SUB_HDR         = 0x28;
	final byte TAG_CORRELATION_ID      = 0x2A;
	final byte TAG_SERVICE_PROVIDER_ID = 0x2B;
	final byte TAG_ENTERPRISE_ID       = 0x2C;
	final byte TAG_VPN_ID              = 0x2D;
	final byte TAG_IDCO                = 0x2E;
	final byte TAG_CALLING_CARD        = 0x2F;

	public FurnishChargingInfoType2() {
		setCode(FCI_TYPE2);
	}

	public String getCorrelationID() {
		return correlationID;
	}
	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}
	public String getServiceProviderId() {
		return serviceProviderId;
	}
	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	public String getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getVpnId() {
		return vpnId;
	}
	public void setVpnId(String vpnId) {
		this.vpnId = vpnId;
	}
	public String getIdco() {
		return idco;
	}
	public void setIdco(String idco) {
		this.idco = idco;
	}
	public String getCallingCard() {
		return callingCard;
	}
	public void setCallingCard(String callingCard) {
		this.callingCard = callingCard;
	}

	/* 
	 * This method is used to encode FCI as defined by Axtel.
	 * (non-Javadoc)
	 * @see com.agnity.inapitutcs2.datatypes.fci.mexico.FurnishChargingMessage#encodeFurnishChargingInfo()
	 */
	@Override
	public byte[] encodeFurnishChargingInfo() throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeFurnishChargingInfo:Enter");
		}

		int totalLen = 0;

		// validate parameters and append length for each field
		totalLen += HDR_LENGTH;
		totalLen += validateCorrelationId();

		// Mandatory Service Provider ID (Corp Id)
		totalLen += HDR_LENGTH;	
		totalLen += validateServiceProviderId();

		// Mandatory Enterprise Id (Account Number) 
		totalLen += HDR_LENGTH;	
		totalLen += validateEnterpriseId();

		// Mandatory VPN ID. Shall be 0 in case of ATF
		totalLen += HDR_LENGTH;	
		totalLen += validateVpnId();

		// Optional IDCO however incase no value is set then 
		// to set header with length as 0
		totalLen += HDR_LENGTH;	

		int len =0;
		if ((len = validateIdco()) != 0 ) {
			totalLen += len;
		}

		// Optional Calling card. However in case no value is set then
		// to set header with length as 0
		len=0;
		totalLen += HDR_LENGTH;
		if ((len = validateCallingCard()) != 0 ) {
			totalLen += len;
		}

		byte[] myParams = new byte[totalLen + FCI_MSG_PREAMBLE_LEN];

		len=0;
		
		// Encode Header
		myParams[len++] = TAG_FCI_HDR;
		myParams[len++] = (byte) (totalLen + 2);
		myParams[len++] = TAG_FCI_SUB_HDR;
		myParams[len++] = (byte) totalLen;
		
		//Encode Correlation ID - BCD Encoding with 0 as leading digit for ODD values
		String digits = correlationID;
		if(digits.length()%2 != 0)
			digits = "0"+digits;

		byte[] encodedCorrelationID = AddressSignal.encodeAdrsSignal(digits);
		myParams[len++] = TAG_CORRELATION_ID;
		myParams[len++] = (byte) encodedCorrelationID.length;
		System.arraycopy(encodedCorrelationID, 0, myParams, len, encodedCorrelationID.length);
		len += encodedCorrelationID.length;

		// ENcode Service Provider ID
		digits = serviceProviderId; 
		if(digits.length()%2 != 0)
			digits = "0"+digits;

		byte [] encodedServiceProviderId = AddressSignal.encodeAdrsSignal(digits);
		myParams[len++] = TAG_SERVICE_PROVIDER_ID;
		myParams[len++] = (byte) encodedServiceProviderId.length;
		System.arraycopy(encodedServiceProviderId, 0, myParams, len, encodedServiceProviderId.length);
		len += encodedServiceProviderId.length;

		// Mandatory Enteprise ID 
		Charset charSet = Charset.forName("US-ASCII");
		byte [] enocdedEnterpriseId = enterpriseId.getBytes(charSet);
		myParams[len++] = TAG_ENTERPRISE_ID;
		myParams[len++] = (byte) enocdedEnterpriseId.length;
		System.arraycopy(enocdedEnterpriseId, 0, myParams, len, enocdedEnterpriseId.length);
		len += enocdedEnterpriseId.length;

		// Mandatory VPN ID
		byte [] encodedVpnId = vpnId.getBytes(charSet);
		myParams[len++] = TAG_VPN_ID;
		myParams[len++] = (byte) encodedVpnId.length;
		System.arraycopy(encodedVpnId, 0, myParams, len, encodedVpnId.length);
		len += encodedVpnId.length;

		// Encode IDCO if set
		if(idco != null && !idco.isEmpty()) {
			digits = idco; 
			if(digits.length()%2 != 0)
				digits = "0"+digits;

			byte [] encodedIdco = AddressSignal.encodeAdrsSignal(digits);
			myParams[len++] = TAG_IDCO;
			myParams[len++] = (byte) encodedIdco.length;
			System.arraycopy(encodedIdco, 0, myParams, len, encodedIdco.length);
			len += encodedIdco.length;
		} else {
			myParams[len++] = TAG_IDCO;
			myParams[len++] = 0x00;
		}

		// Encode Ca;;ing Card if set
		if(callingCard != null && !callingCard.isEmpty()) {
			digits = callingCard; 
			if(digits.length()%2 != 0)
				digits = "0"+digits;

			byte [] encodedCallingCard = AddressSignal.encodeAdrsSignal(digits);
			myParams[len++] = TAG_CALLING_CARD;
			myParams[len++] = (byte) encodedCallingCard.length;
			System.arraycopy(encodedCallingCard, 0, myParams, len, encodedCallingCard.length);
			len += encodedCallingCard.length;
		} else {
			myParams[len++] = TAG_CALLING_CARD;
			myParams[len++] = 0x00;
		}

		if (logger.isInfoEnabled()) {
			logger.info("encodeFciIndicator:" + myParams[0]);
		}

		return myParams;
	}

	/**
	 * This method is used to validate correaltion id. Its mandatory and 
	 * it length could be upto 10 bytes. This is of numeric type. 
	 * @return Total number of bytes for correlation id
	 * @throws InvalidInputException
	 */
	private int validateCorrelationId() throws InvalidInputException {

		// Correlation Id is mandatory and should be 10bytes
		if(null == correlationID || correlationID.isEmpty()) {
			throw new InvalidInputException("FurnishChargingInfoType2: Correlation Id is mandatory");
		}

		if(correlationID.length() > CORRELATION_ID_MAX_LENGTH) {
			throw new InvalidInputException("FurnishChargingInfoType2: Max Correlation ID length could be 20 digits");
		}

		if(!StringUtils.isNumeric(correlationID)) {
			throw new InvalidInputException("FurnishChargingInfoType2: Correlation ID should be numeric:"+correlationID);
		}
		return (correlationID.length()+1)/2;
	}


	/**
	 * This method validates ServiceProvider I. It's manadatory and 
	 * it's length could be upto 14 bytes. This is Digit string. 
	 * @return Total number of bytes for service provider id. 
	 * @throws InvalidInputException
	 */
	private int validateServiceProviderId() throws InvalidInputException {

		// Service Provider Id is mandatory and should be 14 bytes
		if(StringUtils.isEmpty(serviceProviderId)) {
			throw new InvalidInputException("FurnishChargingInfoType2: serviceProviderId is mandatory");
		}

		if(serviceProviderId.length() > SERVICE_PROVIDER_ID_MAX_LENGTH) {
			throw new InvalidInputException("FurnishChargingInfoType2: Max serviceProviderId ID length could be 28 digits");
		}

		if(!StringUtils.isNumeric(serviceProviderId)) {
			throw new InvalidInputException("FurnishChargingInfoType2: serviceProviderId should be numeric:"+serviceProviderId);
		}
		return (serviceProviderId.length()+1)/2;
	}

	/**
	 * This method validates enterprise id. This is mandatory field and 
	 * it's length could be upto 28 bytes. This could be alphanumeric string. 
	 * @return Total number of bytes for enterprise. 
	 * @throws InvalidInputException
	 */
	private int validateEnterpriseId() throws InvalidInputException {

		// Enterprise Provider Id is mandatory and should be 14 bytes
		if(StringUtils.isEmpty(enterpriseId)) {
			throw new InvalidInputException("FurnishChargingInfoType2: enterpriseId is mandatory");
		}

		if(enterpriseId.length() > ENTERPRISE_ID_MAX_LENGTH) {
			throw new InvalidInputException("FurnishChargingInfoType2: Max enterpriseId ID length could be 28 characters");
		}

		return enterpriseId.length();
	}

	/**
	 * This method validates VPN Id. This is mandatory field and 
	 * it's length could be upto 28bytes. This could be alphanumeric string. 
	 * @return Total number of bytes for VPN Id.
	 * @throws InvalidInputException
	 */
	private int validateVpnId() throws InvalidInputException {

		// VPN  Id is mandatory and should be 14 bytes
		if(StringUtils.isEmpty(vpnId)) {
			throw new InvalidInputException("FurnishChargingInfoType2: vpnId is mandatory");
		}

		if(vpnId.length() > VPN_ID_MAX_LENGTH) {
			throw new InvalidInputException("FurnishChargingInfoType2: Max vpnId ID length could be 28 characters");
		}

		return vpnId.length();
	}

	/**
	 * This method validates IDCO. This is optional field. This method checks for maximum 
	 * length of 5 bytes. 
	 * @return total length of IDCO to be encoded. 
	 * @throws InvalidInputException
	 */
	private int validateIdco() throws InvalidInputException {

		// IDCO is optional and coculd be 10 digits
		if(null != idco) {
			if(idco.length() > IDCO_MAX_LENGTH) {
				throw new InvalidInputException("FurnishChargingInfoType2: Idco length could be 10 digits");
			}
			return (idco.length()+1)/2;
		}
		return 0;
	}


	/**
	 * This method validates calling card. This is optional field and will check for maximum length of 5 bytes. 
	 * @return total length of calling cards to be encoded. 
	 * @throws InvalidInputException
	 */
	private int validateCallingCard() throws InvalidInputException {

		// Calling  Id is optional and could be 10 digits
		if(null != callingCard) {
			if(callingCard.length() > CALLING_CARD_MAX_LENGTH) {
				throw new InvalidInputException("FurnishChargingInfoType2: callingCard length could be 10 digits");
			}
			return (callingCard.length()+1)/2;
		}
		return 0;
	}
}
