package com.agnity.map.datatypes;

import org.apache.log4j.*;

/**
 * This Class represents the ATI response  
 * @author sanjay
 *
 */

public class AnyTimeInterrogationResMap {
	
	// Mandatory Parameter
	private SubscriberInfoMap subscriberInfo;
	
	// Optional Parameter
	// TODO:
	// private ExtensionContainerMap extensionContainer; 

	/**
	 * @param subscriberInfo
	 */
	public AnyTimeInterrogationResMap(SubscriberInfoMap subscriberInfo) {
		this.subscriberInfo = subscriberInfo;
	}

	/**
	 * @return the subscriberInfo
	 */
	public SubscriberInfoMap getSubscriberInfo() {
		return subscriberInfo;
	}

	/**
	 * @param subscriberInfo the subscriberInfo to set
	 */
	public void setSubscriberInfo(SubscriberInfoMap subscriberInfo) {
		this.subscriberInfo = subscriberInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnyTimeInterrogationResMap [subscriberInfo=" + subscriberInfo
				+ "]";
	}
	

}
