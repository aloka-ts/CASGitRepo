package com.agnity.map.datatypes;

import org.apache.log4j.*;

import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.datatypes.ImsiDataType;
import com.agnity.map.datatypes.SSEventSpecificationMap;
import com.agnity.map.datatypes.SSInvocationNotificationArgMap;
import com.agnity.map.datatypes.SsCodeMap;
import com.agnity.map.enumdata.CCBSRequestStateEnum;

/**
 * Class for specifying the Argument for ATI request
 * @author sanjay
 *
 */
public class SSInvocationNotificationArgMap {
	// Mandatory Parameters
	private ImsiDataType imsi;
	private ISDNAddressStringMap msisdn;
	private SsCodeMap ssCode;
	private SSEventSpecificationMap ssEventSpecificationMap;
	private ISDNAddressStringMap bSubscriberNumber;
	private CCBSRequestStateEnum ccbsRequestState;
	// Optional Parameters
	// TODO: ExtensionContainer

	private static Logger logger =  Logger.getLogger(SSInvocationNotificationArgMap.class);




	public SSInvocationNotificationArgMap(
			ImsiDataType imsi,
			ISDNAddressStringMap msisdn,
			SsCodeMap ssCode,
			SSEventSpecificationMap ssEventSpecificationMap,
			ISDNAddressStringMap bSubscriberNumber,
			CCBSRequestStateEnum ccbsRequestState

			) {
		this.imsi = imsi;
		this.msisdn = msisdn;
		this.ssCode = ssCode;
		this.ssEventSpecificationMap = ssEventSpecificationMap;
		this.bSubscriberNumber = bSubscriberNumber;
		this.ccbsRequestState = ccbsRequestState;
	}

	public ImsiDataType getImsi() {
		return imsi;
	}

	public void setImsi(ImsiDataType imsi) {
		this.imsi = imsi;
	}

	public ISDNAddressStringMap getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}

	public SsCodeMap getSsCode() {
		return ssCode;
	}

	public void setSsCode(SsCodeMap ssCode) {
		this.ssCode = ssCode;
	}

	public SSEventSpecificationMap getSsEventSpecificationMap() {
		return ssEventSpecificationMap;
	}

	public void setSsEventSpecificationMap(SSEventSpecificationMap ssEventSpecificationMap) {
		this.ssEventSpecificationMap = ssEventSpecificationMap;
	}

	public ISDNAddressStringMap getbSubscriberNumber() {
		return bSubscriberNumber;
	}

	public void setbSubscriberNumber(ISDNAddressStringMap bSubscriberNumber) {
		this.bSubscriberNumber = bSubscriberNumber;
	}

	public CCBSRequestStateEnum getCcbsRequestState() {
		return ccbsRequestState;
	}

	public void setCcbsRequestState(CCBSRequestStateEnum ccbsRequestState) {
		this.ccbsRequestState = ccbsRequestState;
	}
}
