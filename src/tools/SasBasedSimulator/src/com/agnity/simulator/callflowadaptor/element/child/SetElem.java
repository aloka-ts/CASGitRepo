package com.agnity.simulator.callflowadaptor.element.child;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public class SetElem extends Node{
	
	private String varName;
	private String varField;
	private int startIndx;
	private int endIndx;
	
	public SetElem() {
		super(Constants.SET,true);
		setStartIndx(0);
		setEndIndx(-1);
	}

	/**
	 * @param varName the varName to set
	 */
	public void setVarName(String varName) {
		this.varName = varName;
	}

	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @param varField the varField to set
	 */
	public void setVarField(String varField) {
		this.varField = varField.toLowerCase();
	}

	/**
	 * @return the varField
	 */
	public String getVarField() {
		return varField;
	}

	/**
	 * @param startIndx the startIndx to set
	 */
	public void setStartIndx(int startIndx) {
		this.startIndx = startIndx;
	}

	/**
	 * @return the startIndx
	 */
	public int getStartIndx() {
		return startIndx;
	}

	/**
	 * @param endIndx the endIndx to set
	 */
	public void setEndIndx(int endIndx) {
		this.endIndx = endIndx;
	}

	/**
	 * @return the endIndx
	 */
	public int getEndIndx() {
		return endIndx;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.SET);
		builder.append("  ");
		
		builder.append(" VARNAME='");
		builder.append(varName);
		builder.append("'");
		
		builder.append(" VARFieldE='");
		builder.append(varField);
		builder.append("'");
		
		builder.append(" startindx='");
		builder.append(startIndx);
		builder.append("'");
		
		builder.append(" endIndx='");
		builder.append(endIndx);
		builder.append("'");
		
		builder.append(super.toString());
		
		return builder.toString();
	}
	
	
}
