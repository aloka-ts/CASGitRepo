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
package com.agnity.cap.v2.datatypes;

import com.agnity.cap.v2.datatypes.enumType.NaOliInfoCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;

/**
 * ref: ETSI TS 101 046 V7.1.0 (2000-07)
 * @author rnarayan
 * '3D'H – Decimal value 61 - Cellular Service (Type 1)
   '3E'H – Decimal value 62 - Cellular Service (Type 2)
   '3F'H – Decimal value 63 - Cellular Service (roaming)
 */
public class NaOliInfoCapV2 {
 
	private NaOliInfoCapV2Enum naOilinfo;
	
	public NaOliInfoCapV2Enum getNaOilinfo() {
		return naOilinfo;
	}
	
	public void setNaOilinfo(NaOliInfoCapV2Enum naOilinfo) {
		this.naOilinfo = naOilinfo;
	}
	
	public static NaOliInfoCapV2 decode(byte[] bytes) throws InvalidInputException {
	       NaOliInfoCapV2 na = new NaOliInfoCapV2();
	       na.naOilinfo = NaOliInfoCapV2Enum.getValue(bytes[0]);
	       return na;
	}
	
	public static byte[] encode(NaOliInfoCapV2 na) throws InvalidInputException{
	       byte b = (byte) ((na.naOilinfo.getCode()));
	       return new byte[]{b};
	}
	
}
