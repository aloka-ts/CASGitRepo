/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
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

package com.genband.jain.protocol.ss7.tcap;

import java.util.LinkedHashSet;
import java.util.Set;
/**
 * 
opCodes: "0x64 0x05,0x64 0x05,0x64 0x02,0x64 0x17,0x64 0x03"

triggeringRules:
        - ssn: "25"
          opsCode: "0x64 0x03"
          serviceKey: "10"
          appId: "123"
          tt: "111"
        - ssn: "251"
          opsCode: "0x64 0x03"
          appId: "10"
        - ssn: "13"
          opsCode: "9"
          serviceKey: "19"
          appId: "11"
        - ssn: "14"
          appId: "10"
 * @author Madhukar
 *
 */
public class TcapTriggeringCriteriaRule {
	
	private String opCodes;

	LinkedHashSet<TcapTriggeringRule> triggeringRules;

	
	public String getOpCodes() {
		return opCodes;
	}

	public void setOpCodes(String opCodes) {
		this.opCodes = opCodes;
	}

	public LinkedHashSet<TcapTriggeringRule> getTriggeringRules() {
		return triggeringRules;
	}

	public void setTriggeringRules(LinkedHashSet<TcapTriggeringRule> triggeringRules) {
		this.triggeringRules = triggeringRules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((triggeringRules == null) ? 0 : triggeringRules.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TcapTriggeringCriteriaRule other = (TcapTriggeringCriteriaRule) obj;
		if (triggeringRules == null) {
			if (other.triggeringRules != null)
				return false;
		} else if (!triggeringRules.equals(other.triggeringRules))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TcapTriggeringCriteriaRule [opCodes=" + opCodes + ", triggeringRules=" + triggeringRules + "]";
	}
	
	
	
	
}
