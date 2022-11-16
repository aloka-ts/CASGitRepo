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

import com.agnity.inapitutcs2.exceptions.InvalidInputException;

/**
 * @author rarya
 *
 */
public abstract class FurnishChargingMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	int code = -1; 
	
	final int FCI_TYPE1 = 1; // Alestra FCI
	final int FCI_TYPE2 = 2; // Axtel FCI
	
	byte [] fciChrInfo = null;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public abstract byte[] encodeFurnishChargingInfo() throws InvalidInputException;

}
