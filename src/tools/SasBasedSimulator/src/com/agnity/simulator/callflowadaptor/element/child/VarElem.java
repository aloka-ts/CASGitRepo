package com.agnity.simulator.callflowadaptor.element.child;

import java.util.concurrent.atomic.AtomicLong;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public class VarElem extends Node{
	
	private String varName;
	private String value;
	private String nov;
	private int incrementBy;
	AtomicLong longVal;
	
	public VarElem() {
		super(Constants.VAR,true);
		longVal=null;
		this.nov = Constants.NOV_STATIC;
		incrementBy =1;
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
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		if(this.nov.toLowerCase().equals(Constants.NOV_INCREMENT)){
			if(longVal ==null){
				longVal = new AtomicLong(Long.parseLong(value));
			}
			return Long.toString(longVal.getAndAdd(incrementBy));
		}else{
			return value;
		}
	}
	/**
	 * @param nov the nov to set
	 */
	public void setNov(String nov) {
		if(nov!=null && nov.toLowerCase().equals(Constants.NOV_INCREMENT)){
			this.nov = Constants.NOV_INCREMENT;
		}
		
	}
	/**
	 * @return the nov
	 */
	public String getNov() {
		return nov;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.VAR);
		builder.append("  ");
		
		builder.append(" VARNAME='");
		builder.append(varName);
		builder.append("'");
		
		builder.append(" VARVALUE='");
		builder.append(value);
		builder.append("'");
		
		builder.append(" NOV='");
		builder.append(nov);
		builder.append("'");
		
		builder.append(super.toString());
		
		return builder.toString();
	}


	/**
	 * @param incrementBy the incrementBy to set
	 */
	public void setIncrementBy(int incrementBy) {
		this.incrementBy = incrementBy;
	}


	/**
	 * @return the incrementBy
	 */
	public int getIncrementBy() {
		return incrementBy;
	}
	
	
}
