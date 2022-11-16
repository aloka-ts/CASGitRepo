package com.baypackets.ase.util.stpool;

import java.io.Serializable;

public class Token implements Serializable{
	
	protected boolean isUsed=false;
	
	public boolean isUsed() {
		return isUsed;
	}

	protected void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public String getValue() {
		return value;
	}

	protected void setValue(String value) {
		this.value = value;
	}

	protected String value;
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Value: " + value);
		buffer.append("isUsed: " + isUsed);
		return buffer.toString();
	}

}
