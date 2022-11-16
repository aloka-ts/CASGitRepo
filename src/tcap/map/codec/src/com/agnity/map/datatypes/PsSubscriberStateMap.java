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

public class PsSubscriberStateMap {
	
    private boolean notProvidedFromSGSNorMME;
    private boolean psDetached;
    private boolean psAttachedNotReachableForPaging;
    private boolean psAttachedReachableForPaging;
	private PDPContextInfoListMap psPDPActiveNotReachableForPaging;
	private PDPContextInfoListMap psPDPActiveReachableForPaging;
    private NotReachableReasonMapEnum netDetNotReachable;
		
	public PsSubscriberStateMap() {
	}

	public boolean isNotProvidedFromSGSNorMME() {
		return notProvidedFromSGSNorMME;
	}

	public void setNotProvidedFromSGSNorMME(boolean notProvidedFromSGSNorMME) {
		this.notProvidedFromSGSNorMME = notProvidedFromSGSNorMME;
	}

	public boolean isPsDetached() {
		return psDetached;
	}

	public void setPsDetached(boolean psDetached) {
		this.psDetached = psDetached;
	}

	public boolean isPsAttachedNotReachableForPaging() {
		return psAttachedNotReachableForPaging;
	}

	public void setPsAttachedNotReachableForPaging(boolean psAttachedNotReachableForPaging) {
		this.psAttachedNotReachableForPaging = psAttachedNotReachableForPaging;
	}

	public boolean isPsAttachedReachableForPaging() {
		return psAttachedReachableForPaging;
	}

	public void setPsAttachedReachableForPaging(boolean psAttachedReachableForPaging) {
		this.psAttachedReachableForPaging = psAttachedReachableForPaging;
	}

	public PDPContextInfoListMap getPsPDPActiveNotReachableForPaging() {
		return psPDPActiveNotReachableForPaging;
	}

	public void setPsPDPActiveNotReachableForPaging(PDPContextInfoListMap psPDPActiveNotReachableForPaging) {
		this.psPDPActiveNotReachableForPaging = psPDPActiveNotReachableForPaging;
	}

	public PDPContextInfoListMap getPsPDPActiveReachableForPaging() {
		return psPDPActiveReachableForPaging;
	}

	public void setPsPDPActiveReachableForPaging(PDPContextInfoListMap psPDPActiveReachableForPaging) {
		this.psPDPActiveReachableForPaging = psPDPActiveReachableForPaging;
	}

	public NotReachableReasonMapEnum getNetDetNotReachable() {
		return netDetNotReachable;
	}

	public void setNetDetNotReachable(NotReachableReasonMapEnum netDetNotReachable) {
		this.netDetNotReachable = netDetNotReachable;
	}

	@Override
	public String toString() {
		return "PsSubscriberStateMap [notProvidedFromSGSNorMME=" + notProvidedFromSGSNorMME + ", psDetached="
				+ psDetached + ", psAttachedNotReachableForPaging=" + psAttachedNotReachableForPaging
				+ ", psAttachedReachableForPaging=" + psAttachedReachableForPaging
				+ ", psPDPActiveNotReachableForPaging=" + psPDPActiveNotReachableForPaging
				+ ", psPDPActiveReachableForPaging=" + psPDPActiveReachableForPaging + ", netDetNotReachable="
				+ netDetNotReachable + "]";
	}


	
}
