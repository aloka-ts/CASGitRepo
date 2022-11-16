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
package com.agnity.mphdata.common;

import java.io.Serializable;

public class PhoneNumber implements Serializable, Cloneable {
	private static final long	serialVersionUID	= -7123530386775686406L;

	//Nature of Addresses
	public static final int		NOA_UNKNOWN			= 2;
	public static final int		NOA_NATIONAL		= 3;
	public static final int		NOA_INTERNATIONAL	= 4;

	//Number Plans
	public static final int		NP_UNKNOWN			= 6;
	public static final int		NP_PRIVATE			= 5;
	public static final int		NP_ISDN				= 1;

	private String				address;

	private int					natureOfAddress;								//2- UNKNOWN,3- NATIONAL,4- INTERNATIONAL

	private int					numberingPlan;									//Added for sbtm:6: UNKNOWN,5: PRIVATE,1: ISDN

	//OLEC for caller
	private int					localExCarrier;								//Added for sbtm

	private int					presentationIndicator;							//Added for sbtm:Presentation Indicator

	private int					numberOverriding	= -1;

	public int getLocalExCarrier() {
		return localExCarrier;
	}

	public void setLocalExCarrier(int localExCarrier) {
		this.localExCarrier = localExCarrier;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String number) {
		this.address = number;
	}

	public int getNatureOfAddress() {
		return natureOfAddress;
	}

	public void setNatureOfAddress(int natureOfAddress) {
		this.natureOfAddress = natureOfAddress;
	}

	public int getNumberingPlan() {
		return numberingPlan;
	}

	public void setNumberingPlan(int numberingPlan) {
		this.numberingPlan = numberingPlan;
	}

	@Override
	public PhoneNumber clone() {
		PhoneNumber pn = new PhoneNumber();
		pn.address = this.address == null ? null : new String(this.address);
		pn.localExCarrier = this.localExCarrier;
		pn.natureOfAddress = this.natureOfAddress;
		pn.numberingPlan = this.numberingPlan;
		pn.presentationIndicator = this.presentationIndicator;
		return pn;
	}

	public int getPresentationIndicator() {
		return presentationIndicator;
	}

	public void setPresentationIndicator(int presentationIndicator) {
		this.presentationIndicator = presentationIndicator;
	}

	public int getNumberOverriding() {
		return numberOverriding;
	}

	public void setNumberOverriding(int value) {
		numberOverriding = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhoneNumber [address=");
		builder.append(address);
		builder.append(", localExCarrier=");
		builder.append(localExCarrier);
		builder.append(", natureOfAddress=");
		builder.append(natureOfAddress);
		builder.append(", numberOverriding=");
		builder.append(numberOverriding);
		builder.append(", numberingPlan=");
		builder.append(numberingPlan);
		builder.append(", presentationIndicator=");
		builder.append(presentationIndicator);
		builder.append("]");
		return builder.toString();
	}
}
