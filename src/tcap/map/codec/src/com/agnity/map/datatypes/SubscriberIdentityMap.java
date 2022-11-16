/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.datatypes;

public class SubscriberIdentityMap {

	private ImsiDataType imsi;
	private ISDNAddressStringMap msisdn;
	
	public SubscriberIdentityMap(ImsiDataType imsi) {
		this.imsi = imsi;
	}
	
	public SubscriberIdentityMap(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
		
	}
	/**
	 * Method to return IMSI
	 * @return ImsiDataType
	 */
	
	public ImsiDataType getImsi() {
		return this.imsi;
	}
	
	/**
	 * 
	 * @return MSISDN 
	 */
	
	public AddressStringMap getMsisdn() {
		return this.msisdn;
	}
}
