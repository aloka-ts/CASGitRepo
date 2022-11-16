package com.agnity.map.datatypes;

public class ForwardingDataMap {
	private ISDNAddressStringMap forwardedToNumber;
	private ISDNAddressStringMap forwardedToSubaddress;
	private ForwOptionsMap forwardingOptions;
	private FtnAddressStringMap longForwardedToNumber;
	/**
	 * @return the forwardedToNumber
	 */
	public ISDNAddressStringMap getForwardedToNumber() {
		return forwardedToNumber;
	}
	/**
	 * @return the forwardedToSubaddress
	 */
	public ISDNAddressStringMap getForwardedToSubaddress() {
		return forwardedToSubaddress;
	}
	/**
	 * @return the forwardingOptions
	 */
	public ForwOptionsMap getForwardingOptions() {
		return forwardingOptions;
	}
	/**
	 * @return the longForwardedToNumber
	 */
	public FtnAddressStringMap getLongForwardedToNumber() {
		return longForwardedToNumber;
	}
	/**
	 * @param forwardedToNumber the forwardedToNumber to set
	 */
	public void setForwardedToNumber(ISDNAddressStringMap forwardedToNumber) {
		this.forwardedToNumber = forwardedToNumber;
	}
	/**
	 * @param forwardedToSubaddress the forwardedToSubaddress to set
	 */
	public void setForwardedToSubaddress(ISDNAddressStringMap forwardedToSubaddress) {
		this.forwardedToSubaddress = forwardedToSubaddress;
	}
	/**
	 * @param forwardingOptions the forwardingOptions to set
	 */
	public void setForwardingOptions(ForwOptionsMap forwardingOptions) {
		this.forwardingOptions = forwardingOptions;
	}
	/**
	 * @param longForwardedToNumber the longForwardedToNumber to set
	 */
	public void setLongForwardedToNumber(FtnAddressStringMap longForwardedToNumber) {
		this.longForwardedToNumber = longForwardedToNumber;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ForwardingDataMap [forwardedToNumber=" + forwardedToNumber
				+ ", forwardedToSubaddress=" + forwardedToSubaddress
				+ ", forwardingOptions=" + forwardingOptions
				+ ", longForwardedToNumber=" + longForwardedToNumber + "]";
	}
	
	
}
