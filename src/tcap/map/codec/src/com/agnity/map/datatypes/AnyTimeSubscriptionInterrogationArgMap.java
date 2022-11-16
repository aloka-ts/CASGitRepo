package com.agnity.map.datatypes;

import org.apache.log4j.*;

public class AnyTimeSubscriptionInterrogationArgMap {
	
	// Mandatory attributes
	private SubscriberIdentityMap subIdentity;
	private RequestedSubscriptionInfoMap requestedSubsInfo;
	private ISDNAddressStringMap gsmScfAddr;
	
	// Optional attributes
	// TODO: ExtensionContainer
	
	private static Logger logger =  Logger.getLogger(AnyTimeSubscriptionInterrogationArgMap.class);
	
	/**
	 * 
	 */
	public AnyTimeSubscriptionInterrogationArgMap(SubscriberIdentityMap subscriberIdentity, 
			RequestedSubscriptionInfoMap requestedSubsInfo, ISDNAddressStringMap gsmSCFAddress) {
		this.subIdentity = subscriberIdentity;
		this.requestedSubsInfo = requestedSubsInfo;
		this.gsmScfAddr = gsmSCFAddress;
	}
	
	public  SubscriberIdentityMap getSubscriberIdentity() {
		return this.subIdentity;
	}
	
	public RequestedSubscriptionInfoMap getRequestedSubsInfo() {
		return this.requestedSubsInfo;
	}
	
	public ISDNAddressStringMap getGsmScfAddress() {
		return this.gsmScfAddr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnyTimeSubscriptionInterrogationArgMap [subIdentity="
				+ subIdentity + ", requestedSubsInfo=" + requestedSubsInfo
				+ ", gsmScfAddr=" + gsmScfAddr + "]";
	}
	
	
}
