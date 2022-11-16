package com.baypackets.ase.container;

/**
 * This enum value contains the valid values in process-annotation tag of
 * sas.xml
 * 
 * @author averma
 * 
 */
public enum ProcessAnnotation {

	ENABLE("enable"), DISABLE("disable"), ENABLE_ALL("enableAll");

	private String value;

	private ProcessAnnotation(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}