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

import com.agnity.map.exceptions.InvalidInputException;

/**
 * 
 *  This type is used to represent forwarded-to numbers.
 *  If NAI = international the first digits represent the country code (CC)
 *  and the network destination code (NDC) as for E.164.
 * 
 * @author sanjay
 *
 */
public class FtnAddressStringMap extends AddressStringMap {
	
	public FtnAddressStringMap(byte[] address) throws InvalidInputException {
		super(address);
	}
	
	public FtnAddressStringMap() {
		super();
	}

}
