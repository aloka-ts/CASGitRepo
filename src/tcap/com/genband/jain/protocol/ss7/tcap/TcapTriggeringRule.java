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

import org.apache.commons.lang3.StringUtils;

/**
 * Rule to find app 
 * @author Madhukar
 *
 */
public class TcapTriggeringRule {

	private String ssn;
	
	private String opsCode;
	
	private String serviceKey;
	
	private String appId;
	
	private String tt; // translation Type (optional). In case not need to be ingnored
					   // then should not be defined. Even if it is received it will not
					   // be checked

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getOpsCode() {
		return opsCode;
	}

	public void setOpsCode(String opsCode) {
		this.opsCode = opsCode;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String getTt(){
		return tt;
	}
	
	public Integer getTt(int a) {
		Integer retval = null;
		if(StringUtils.isNotBlank(tt)){
			try{
			retval = Integer.valueOf(tt);
			}catch(Exception ep){}
		}
		return retval;
	}

	public void setTt(String tt) {
		this.tt = tt;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((opsCode == null) ? 0 : opsCode.hashCode());
		result = prime * result + ((serviceKey == null) ? 0 : serviceKey.hashCode());
		result = prime * result + ((ssn == null) ? 0 : ssn.hashCode());
		result = prime * result + ((tt == null) ? 0 : tt.hashCode());
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
		TcapTriggeringRule other = (TcapTriggeringRule) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (opsCode == null) {
			if (other.opsCode != null)
				return false;
		} else if (!opsCode.equals(other.opsCode))
			return false;
		if (serviceKey == null) {
			if (other.serviceKey != null)
				return false;
		} else if (!serviceKey.equals(other.serviceKey))
			return false;
		if (ssn == null) {
			if (other.ssn != null)
				return false;
		} else if (!ssn.equals(other.ssn))
			return false;
		if (tt == null) {
			if (other.tt != null)
				return false;
		} else if (!tt.equals(other.tt))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TcapTriggeringRule [ssn=" + ssn + ", opsCode=" + opsCode + ", serviceKey=" + serviceKey + ", appId=" + appId
				+ ", tt=" + tt + "]";
	}

	
}
