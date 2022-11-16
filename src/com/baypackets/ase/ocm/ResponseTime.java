/*
 * Created on Jan 26, 2005
 *
 */
package com.baypackets.ase.ocm;

/**
 * @author Dana
 *
 */
public class ResponseTime extends OverloadParameter {
	ResponseTime(int id, String name, Type type) {
		super(id, name, type);
	}
	
	public float getOlf() {
		return value*weight;
	}

	public boolean isLimitReached() {
		// does not support checking limit
		return false;
	}
	
	public void setLimit(float max) {
		// override super class method to do nothing
	}
	
	public void increase(float amount) {
		// override super class method to do nothing
	}
	
	public void decrease(float amount) {
		// override super class method to do nothing
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append(": id=");
		buf.append(String.valueOf(id));
		buf.append(" weight=");
		buf.append(String.valueOf(weight));
		buf.append(" value=");
		buf.append(String.valueOf(value));
		return buf.toString();
	}
}
