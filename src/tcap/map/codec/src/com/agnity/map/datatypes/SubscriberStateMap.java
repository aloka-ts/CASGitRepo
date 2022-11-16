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

import com.agnity.map.enumdata.NotReachableReasonMapEnum;

public class SubscriberStateMap {

	private boolean assumedIdle;
	private boolean camelBusy;
	private NotReachableReasonMapEnum netDetNotReachable;
	private boolean notProvidedFromVLR;
	
	
	public SubscriberStateMap(boolean assumedIdle, boolean camelBusy,
			NotReachableReasonMapEnum netDetNotReachable,
			boolean notProvidedFromVLR) {
		this.assumedIdle = assumedIdle;
		this.camelBusy = camelBusy;
		this.netDetNotReachable = netDetNotReachable;
		this.notProvidedFromVLR = notProvidedFromVLR;
	}

	
	public SubscriberStateMap() {
	}


	public boolean isAssumedIdle() {
		return assumedIdle;
	}


	public void setAssumedIdle(boolean assumedIdle) {
		this.assumedIdle = assumedIdle;
	}


	public boolean isCamelBusy() {
		return camelBusy;
	}


	public void setCamelBusy(boolean camelBusy) {
		this.camelBusy = camelBusy;
	}


	public NotReachableReasonMapEnum getNotReachableReason() {
		return netDetNotReachable;
	}


	public void setNetDetNotReachableReason(NotReachableReasonMapEnum netDetNotReachable) {
		System.out.println("inside netDetNotReachable"+netDetNotReachable);
		this.netDetNotReachable = netDetNotReachable;
	}


	public boolean isNotProvidedFromVLR() {
		return notProvidedFromVLR;
	}


	public void setNotProvidedFromVLR(boolean notProvidedFromVLR) {
		this.notProvidedFromVLR = notProvidedFromVLR;
	}


	@Override
	public String toString() {
		return "SubscriberStateMap [assumedIdle=" + assumedIdle
				+ ", camelBusy=" + camelBusy + ", netDetNotReachable="
				+ netDetNotReachable + ", notProvidedFromVLR="
				+ notProvidedFromVLR + "]";
	}

	
	
}
