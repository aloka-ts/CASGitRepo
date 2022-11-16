package com.agnity.simulator.callflowadaptor.element.child;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public class HeaderElem extends Node{
	
	private String name;
	private String value;
	
	public HeaderElem() {
		super(Constants.HEADER,true);
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.HEADER);
		builder.append("  ");
		
		builder.append(" header name='");
		builder.append(name);
		builder.append("'");
		
		builder.append(" header value='");
		builder.append(value);
		builder.append("'");
		
		builder.append(super.toString());
		
		return builder.toString();
	}
	
	
}
