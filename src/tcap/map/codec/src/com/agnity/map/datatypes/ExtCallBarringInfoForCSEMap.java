package com.agnity.map.datatypes;

import java.util.Collection;

public class ExtCallBarringInfoForCSEMap {
	// Mandatory parameters
	private SsCodeMap sscode;  
	private Collection<ExtCallBarringFeatureMap> callBarFeatureList;
	
	// Optional Parameters
	private String password;
	private Integer wrongPasswordAttemptsCounter;
	
	//TODO: notificationToCSE, ExtensionContainer

	/**
	 * Constructor with mandatory parameters
	 */
	
	public ExtCallBarringInfoForCSEMap(SsCodeMap sscode, 
			Collection<ExtCallBarringFeatureMap> cbFeatureList) {
		this.sscode = sscode;
		this.callBarFeatureList = cbFeatureList;
	}
	
	/**
	 * @return the sscode
	 */
	public SsCodeMap getSscode() {
		return sscode;
	}
	/**
	 * @return the callBarFeatureList
	 */
	public Collection<ExtCallBarringFeatureMap> getCallBarFeatureList() {
		return callBarFeatureList;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @return the wrongPasswordAttemptsCounter
	 */
	public Integer getWrongPasswordAttemptsCounter() {
		return wrongPasswordAttemptsCounter;
	}
	/**
	 * @param sscode the sscode to set
	 */
	public void setSscode(SsCodeMap sscode) {
		this.sscode = sscode;
	}
	/**
	 * @param callBarFeatureList the callBarFeatureList to set
	 */
	public void setCallBarFeatureList(
			Collection<ExtCallBarringFeatureMap> callBarFeatureList) {
		this.callBarFeatureList = callBarFeatureList;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @param wrongPasswordAttemptsCounter the wrongPasswordAttemptsCounter to set
	 */
	public void setWrongPasswordAttemptsCounter(Integer wrongPasswordAttemptsCounter) {
		this.wrongPasswordAttemptsCounter = wrongPasswordAttemptsCounter;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtCallBarringInfoForCSEMap [sscode=" + sscode
				+ ", callBarFeatureList=" + callBarFeatureList + ", password="
				+ password + ", wrongPasswordAttemptsCounter="
				+ wrongPasswordAttemptsCounter + "]";
	}
	

		
}
