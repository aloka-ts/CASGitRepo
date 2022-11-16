package com.agnity.map.datatypes;

import java.util.Collection;

public class CallBarringDataMap {
	
	// Mandatory attribute
	private Collection<ExtCallBarringFeatureMap> callBarringFeatureList;
	
	// optional attribute
	private String password;
	private Integer wrongPasswordAttemptsCounter;


	/**
	 * @return the callBarringFeatureList
	 */
	public Collection<ExtCallBarringFeatureMap> getCallBarringFeatureList() {
		return callBarringFeatureList;
	}
	/**
	 * @param callBarringFeatureList the callBarringFeatureList to set
	 */
	public void setCallBarringFeatureList(
			Collection<ExtCallBarringFeatureMap> callBarringFeatureList) {
		this.callBarringFeatureList = callBarringFeatureList;
	}

	/**
	 * TODO:
	 * 1. notificationToCSE NULL OPTIONAL
	 * 2. extensionContainer ExtensionContainer OPTIONAL
	 */
	
	
	
	/**
	 * @param callBarringFeatureList
	 */
	public CallBarringDataMap(
			Collection<ExtCallBarringFeatureMap> callBarringFeatureList) {
		this.callBarringFeatureList = callBarringFeatureList;
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
	/**
	 * @param callBarringFeatureList
	 * @param password
	 * @param wrongPasswordAttemptsCounter
	 */
	public CallBarringDataMap(
			Collection<ExtCallBarringFeatureMap> callBarringFeatureList,
			String password, Integer wrongPasswordAttemptsCounter) {
		this.callBarringFeatureList = callBarringFeatureList;
		this.password = password;
		this.wrongPasswordAttemptsCounter = wrongPasswordAttemptsCounter;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CallBarringDataMap [callBarringFeatureList="
				+ callBarringFeatureList + ", password=" + password
				+ ", wrongPasswordAttemptsCounter="
				+ wrongPasswordAttemptsCounter + "]";
	}

	
}
