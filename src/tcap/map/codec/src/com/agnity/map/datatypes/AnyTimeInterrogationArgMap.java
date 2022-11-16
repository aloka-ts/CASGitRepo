package com.agnity.map.datatypes;

import org.apache.log4j.*;

/**
 * Class for specifying the Argument for ATI request
 * @author sanjay
 *
 */
public class AnyTimeInterrogationArgMap {
	
	// Mandatory Parameters
	private SubscriberIdentityMap subIdentity;
	private RequestedInfoMap requestedInfo;
	private ISDNAddressStringMap gsmScfAddr;
	
	// Optional Parameters
	// TODO: ExtensionContainer
	
	private static Logger logger =  Logger.getLogger(AnyTimeInterrogationArgMap.class);
	
	/**
	 * 
	 */
	public AnyTimeInterrogationArgMap(SubscriberIdentityMap subscriberIdentity, 
			RequestedInfoMap requestedInfo, ISDNAddressStringMap gsmSCFAddress) {
		this.subIdentity = subscriberIdentity;
		this.requestedInfo = requestedInfo;
		this.gsmScfAddr = gsmSCFAddress;
	}
	
	public  SubscriberIdentityMap getSubscriberIdentity() {
		return this.subIdentity;
	}
	
	public RequestedInfoMap getRequestedInfo() {
		return this.requestedInfo;
	}
	
	public ISDNAddressStringMap getGsmScfAddress() {
		return this.gsmScfAddr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnyTimeInterrogationArgMap [subIdentity=" + subIdentity
				+ ", requestedInfo=" + requestedInfo + ", gsmScfAddr="
				+ gsmScfAddr + "]";
	}
	
	
}
