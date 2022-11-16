/**
 * MsrelaySpec.java
 * 
 * This class is created for MSML Dialog Transfrom Package support for rfc 5707.<br>
 * This class will be used for relay tag under dialog transform package of msml.<br>
 * This class includes getters and setters for manipulating attributes of relay element.  
 * 
 *  
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.transform;

import java.io.Serializable;

public class MsRelaySpec implements Serializable{
	private String id;

	/**
	 * This method sets id for relay element. Optional attribute.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method sets id for relay element. Optional attribute.
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
}
