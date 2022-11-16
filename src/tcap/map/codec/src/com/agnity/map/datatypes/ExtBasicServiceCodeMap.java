package com.agnity.map.datatypes;

/**
 * This class represents an ASN choice. 
 * Class shall host either of two service objects viz
 * ExtBearerServiceCodeMode or ExtTelesserviceCode Map
 * 
 * @author sanjay
 *
 */

public class ExtBasicServiceCodeMap {
	private ExtBearerServiceCodeMap extBearerService;
	private ExtTeleserviceCodeMap extTeleservice;
	
	public ExtBasicServiceCodeMap(ExtBearerServiceCodeMap bcCode){
		this.extBearerService = bcCode;
	}
	
	public ExtBasicServiceCodeMap(ExtTeleserviceCodeMap tcCode) {
		this.extTeleservice = tcCode;
	}

	/**
	 * @return the extBearerService
	 */
	public ExtBearerServiceCodeMap getExtBearerService() {
		return extBearerService;
	}

	/**
	 * @return the extTeleservice
	 */
	public ExtTeleserviceCodeMap getExtTeleservice() {
		return extTeleservice;
	}

	/**
	 * @param extBearerService the extBearerService to set
	 */
	public void setExtBearerService(ExtBearerServiceCodeMap extBearerService) {
		this.extBearerService = extBearerService;
	}

	/**
	 * @param extTeleservice the extTeleservice to set
	 */
	public void setExtTeleservice(ExtTeleserviceCodeMap extTeleservice) {
		this.extTeleservice = extTeleservice;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtBasicServiceCodeMap [extBearerService=" + extBearerService
				+ ", extTeleservice=" + extTeleservice + "]";
	}
	
	
}
