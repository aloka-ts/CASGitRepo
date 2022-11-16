/**
 * 
 */
package com.agnity.simulator.domainobjects;

/**
 * @author saneja
 *
 */
public class Header {
	
	private String headerName;
	private String headerValue;
	
	public Header(String headerName, String headerValue) {
		this.headerName=headerName;
		this.headerValue=headerValue;
	}

	@Override
	public boolean equals(Object paramObject) {
		if(paramObject == this){
			return true;
		}else if(paramObject instanceof Header){
			String otherVarName = ((Header)paramObject).getHeaderName();
			return headerName.equals(otherVarName);
		}else{
			return false;
		}
	}

	/**
	 * @param headerName the headerName to set
	 */
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	/**
	 * @return the headerName
	 */
	public String getHeaderName() {
		return headerName;
	}

	/**
	 * @param headerValue the headerValue to set
	 */
	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	/**
	 * @return the headerValue
	 */
	public String getHeaderValue() {
		return headerValue;
	}
	
	
	

}
