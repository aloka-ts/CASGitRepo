package com.agnity.map.datatypes;

import com.agnity.map.enumdata.BearerServiceCodeMapEnum;
import com.agnity.map.enumdata.TeleServiceCodeMapEnum;

public class BasicServiceCodeMap {
	private BearerServiceCodeMapEnum bearerServiceCode;
	private TeleServiceCodeMapEnum teleServiceCode;
	/**
	 * @param bearerServiceCode
	 */
	public BasicServiceCodeMap(BearerServiceCodeMapEnum bearerServiceCode) {
		this.bearerServiceCode = bearerServiceCode;
	}
	/**
	 * @param teleServiceCode
	 */
	public BasicServiceCodeMap(TeleServiceCodeMapEnum teleServiceCode) {
		this.teleServiceCode = teleServiceCode;
	}
	/**
	 * @return the bearerServiceCode
	 */
	public BearerServiceCodeMapEnum getBearerServiceCode() {
		return bearerServiceCode;
	}
	/**
	 * @param bearerServiceCode the bearerServiceCode to set
	 */
	public void setBearerServiceCode(BearerServiceCodeMapEnum bearerServiceCode) {
		this.bearerServiceCode = bearerServiceCode;
	}
	/**
	 * @return the teleServiceCode
	 */
	public TeleServiceCodeMapEnum getTeleServiceCode() {
		return teleServiceCode;
	}
	/**
	 * @param teleServiceCode the teleServiceCode to set
	 */
	public void setTeleServiceCode(TeleServiceCodeMapEnum teleServiceCode) {
		this.teleServiceCode = teleServiceCode;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasicServiceCodeMap [bearerServiceCode=" + bearerServiceCode
				+ ", teleServiceCode=" + teleServiceCode + "]";
	}
	
	
}
