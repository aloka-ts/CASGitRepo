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

import com.agnity.map.enumdata.NumberPortabilityStatusMapEnum;

public class MnpInfoResMap {

	private String routeingNumber;
	private ImsiDataType imsi;
	private ISDNAddressStringMap msisdn;
	private NumberPortabilityStatusMapEnum npStatus;
	// TODO: 
	// ExtensionContainerMap
	
	public MnpInfoResMap(String routeingNumber,
			ImsiDataType imsi, ISDNAddressStringMap msisdn,
			NumberPortabilityStatusMapEnum npStatus) {
		this.routeingNumber = routeingNumber;
		this.imsi = imsi;
		this.msisdn = msisdn;
		this.npStatus = npStatus;
	}
	
	public MnpInfoResMap() {
		
	}

	/**
	 * @return the routeingNumber
	 */
	public String getRouteingNumber() {
		return routeingNumber;
	}

	/**
	 * @param routeingNumber the routeingNumber to set
	 */
	public void setRouteingNumber(String routeingNumber) {
		this.routeingNumber = routeingNumber;
	}

	/**
	 * @return the imsi
	 */
	public ImsiDataType getImsi() {
		return imsi;
	}

	/**
	 * @param imsi the imsi to set
	 */
	public void setImsi(ImsiDataType imsi) {
		this.imsi = imsi;
	}

	/**
	 * @return the msisdn
	 */
	public ISDNAddressStringMap getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * @return the npStatus
	 */
	public NumberPortabilityStatusMapEnum getNpStatus() {
		return npStatus;
	}

	/**
	 * @param npStatus the npStatus to set
	 */
	public void setNpStatus(NumberPortabilityStatusMapEnum npStatus) {
		this.npStatus = npStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MnpInfoResMap [routeingNumber=" + routeingNumber + ", imsi="
				+ imsi + ", msisdn=" + msisdn + ", npStatus=" + npStatus + "]";
	}
	
}
