/*
 * Created on Sep 4, 2012 
*/
package com.baypackets.ase.util;

import java.io.Serializable;

public class AseTimerInfo implements Serializable{
	
	private static final long serialVersionUID = 33684982478354L;
	private String sbbName;
	
	public String getSbbName() {
		return sbbName;
	}
	
	public void setSbbName(String sbbName) {
		this.sbbName = sbbName;
	}
	
}