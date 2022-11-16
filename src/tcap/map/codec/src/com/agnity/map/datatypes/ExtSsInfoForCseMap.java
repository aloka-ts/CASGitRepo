package com.agnity.map.datatypes;

public class ExtSsInfoForCseMap {
	private ExtForwardingInfoForCSEMap forwardingInfoForCSE;
	private ExtCallBarringInfoForCSEMap callBarringInfoForCSE;
	/**
	 * @param forwardingInfoForCSE
	 */
	public ExtSsInfoForCseMap(ExtForwardingInfoForCSEMap forwardingInfoForCSE) {
		this.forwardingInfoForCSE = forwardingInfoForCSE;
	}
	/**
	 * @param callBarringInfoForCSE
	 */
	public ExtSsInfoForCseMap(ExtCallBarringInfoForCSEMap callBarringInfoForCSE) {
		this.callBarringInfoForCSE = callBarringInfoForCSE;
	}
	/**
	 * @return the forwardingInfoForCSE
	 */
	public ExtForwardingInfoForCSEMap getForwardingInfoForCSE() {
		return forwardingInfoForCSE;
	}
	/**
	 * @return the callBarringInfoForCSE
	 */
	public ExtCallBarringInfoForCSEMap getCallBarringInfoForCSE() {
		return callBarringInfoForCSE;
	}
	/**
	 * @param forwardingInfoForCSE the forwardingInfoForCSE to set
	 */
	public void setForwardingInfoForCSE(
			ExtForwardingInfoForCSEMap forwardingInfoForCSE) {
		this.forwardingInfoForCSE = forwardingInfoForCSE;
	}
	/**
	 * @param callBarringInfoForCSE the callBarringInfoForCSE to set
	 */
	public void setCallBarringInfoForCSE(
			ExtCallBarringInfoForCSEMap callBarringInfoForCSE) {
		this.callBarringInfoForCSE = callBarringInfoForCSE;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtSsInfoForCseMap [forwardingInfoForCSE="
				+ forwardingInfoForCSE + ", callBarringInfoForCSE="
				+ callBarringInfoForCSE + "]";
	}
}
