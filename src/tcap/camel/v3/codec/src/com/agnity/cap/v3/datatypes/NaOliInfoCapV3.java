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
package com.agnity.cap.v3.datatypes;

import com.agnity.cap.v3.datatypes.enumType.NaOliInfoCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;

/**
 * ref: ETSI TS 101 046 V7.1.0 (2000-07)
 * @author rnarayan
 * '3D'H – Decimal value 61 - Cellular Service (Type 1)
   '3E'H – Decimal value 62 - Cellular Service (Type 2)
   '3F'H – Decimal value 63 - Cellular Service (roaming)
 */
public class NaOliInfoCapV3 {
 
	private NaOliInfoCapV3Enum naOilinfo;
	
	public NaOliInfoCapV3Enum getNaOilinfo() {
		return naOilinfo;
	}
	
	public void setNaOilinfo(NaOliInfoCapV3Enum naOilinfo) {
		this.naOilinfo = naOilinfo;
	}
	
	public static NaOliInfoCapV3 decode(byte[] bytes) throws InvalidInputException {
	       NaOliInfoCapV3 na = new NaOliInfoCapV3();
	       na.naOilinfo = NaOliInfoCapV3Enum.getValue(bytes[0]);
	       return na;
	}
	
	public static byte[] encode(NaOliInfoCapV3 na) throws InvalidInputException{
	       byte b = (byte) ((na.naOilinfo.getCode()));
	       return new byte[]{b};
	}
	
}
