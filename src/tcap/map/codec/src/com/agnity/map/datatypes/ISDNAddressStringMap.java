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


import org.apache.log4j.Logger;

import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;
/**
 * 
 * @author sanjay
 */
public class ISDNAddressStringMap extends AddressStringMap {
	
	public ISDNAddressStringMap(byte[] address) throws InvalidInputException {
		super(address);
	}
	
	public ISDNAddressStringMap() {
		super();
	}
}
