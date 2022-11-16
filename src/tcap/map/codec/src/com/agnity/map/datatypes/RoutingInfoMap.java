package com.agnity.map.datatypes;

public class RoutingInfoMap {
	private ISDNAddressStringMap roamingNumber;
	private ForwardingDataMap forwardingData;
	
	
	/**
	 * @param roamingNumber
	 */
	public RoutingInfoMap(ISDNAddressStringMap roamingNumber) {
		this.roamingNumber = roamingNumber;
	}

	/**
	 * @param forwardingData
	 */
	public RoutingInfoMap(ForwardingDataMap forwardingData) {
		this.forwardingData = forwardingData;
	}


	/**
	 * @return the roamingNumber
	 */
	public ISDNAddressStringMap getRoamingNumber() {
		return roamingNumber;
	}
	/**
	 * @return the forwardingData
	 */
	public ForwardingDataMap getForwardingData() {
		return forwardingData;
	}
	/**
	 * @param roamingNumber the roamingNumber to set
	 */
	public void setRoamingNumber(ISDNAddressStringMap roamingNumber) {
		this.roamingNumber = roamingNumber;
	}
	/**
	 * @param forwardingData the forwardingData to set
	 */
	public void setForwardingData(ForwardingDataMap forwardingData) {
		this.forwardingData = forwardingData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RoutingInfoMap [roamingNumber=" + roamingNumber
				+ ", forwardingData=" + forwardingData + "]";
	}
}