package com.agnity.map.datatypes;

public class ServiceKeyMap {
	
	private Integer serviceKey;

	/**
	 * @param serviceKey
	 */
	public ServiceKeyMap(Integer serviceKey) {
		this.serviceKey = serviceKey;
	}

	/**
	 * @return the serviceKey
	 */
	public Integer getServiceKey() {
		return serviceKey;
	}

	/**
	 * @param serviceKey the serviceKey to set
	 */
	public void setServiceKey(Integer serviceKey) {
		this.serviceKey = serviceKey;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceKeyMap [serviceKey=" + serviceKey + "]";
	}
}
