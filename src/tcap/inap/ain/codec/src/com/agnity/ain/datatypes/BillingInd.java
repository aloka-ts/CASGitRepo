/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.datatypes;

import org.apache.log4j.Logger;
import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Util;

/**
 * @author nishantsharma
 *
 */
public class BillingInd extends AddressSignal 
{
	private static Logger logger = Logger.getLogger(BillingInd.class);
	private String aMAcallType;
	private String serviceFeature;
	/**
	 * This Method encodes the Billing Indicator parameter
	 * This parameter is used in Analyze route and Disconnect operation etc. 
	 * and parameter which are  based on it like PrimaryBillingIndicator
	 * @return a byte array
	 * @throws InvalidInputException
	 */
	public  byte[] encodeBillingInd() throws InvalidInputException{
		if (logger.isInfoEnabled()){
			logger.info("encodeBillingInd:Enter");
		}
		int i=0;
		byte [] billingindicator=new byte[4];
		byte [] amaCallType=AddressSignal.encodeAdrsSignal(aMAcallType);
		byte [] srvFeature=AddressSignal.encodeAdrsSignal(serviceFeature);
		for (int j = 0; j < amaCallType.length; j++){
			billingindicator[i++] = amaCallType[j];
		}
		for (int j = 0; j < srvFeature.length; j++) {
			billingindicator[i++] = srvFeature[j];
		}
		if(logger.isDebugEnabled()){
			logger.debug("encodeBillingInd:Encoded data: "+ Util.formatBytes(billingindicator));
		}
		if (logger.isInfoEnabled()){
			logger.info("encodeBillingInd:Exit");
		}
		return billingindicator;
		
	}
	
	/**
	 * This Method decodes the Billing Indicator parameter
	 * and parameter which are  based on it like PrimaryBillingIndicator
	 * @return Billing Indicator object
	 * @param Byte array
	 * @throws InvalidInputException
	 */
	public  BillingInd decodeBillingInd(byte[] data)throws InvalidInputException{
		if (logger.isInfoEnabled()){
			logger.info("decodeBillingInd:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		String billingInd=AddressSignal.decodeAdrsSignal(data,0,0);
		this.aMAcallType=billingInd.substring(0,3);
		this.serviceFeature=billingInd.substring(4,7);
		return this;
	}
	
	public String toString(){
		String obj = "aMAcallType:"+ aMAcallType+"serviceFeature:"+ serviceFeature ;
		return obj ;
	}
	
	public String getaMAcallType() {
		return aMAcallType;
	}
	public void setaMAcallType(String aMAcallType) {
		this.aMAcallType = aMAcallType;
	}
	public String getServiceFeature() {
		return serviceFeature;
	}
	public void setServiceFeature(String serviceFeature) {
		this.serviceFeature = serviceFeature;
	}
	
}
