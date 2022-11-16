/**
 * MsClampSpec.java
 * 
 * This class is created for MSML Dialog Transfrom Package support for rfc 5707.<br>
 * This class will be used for clamp tag under dialog transform package of msml.<br>
 * This class includes getters and setters for manipulating attributes of clamp element.  
 * 
 *  
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.transform;

import java.io.Serializable;

public class MsClampSpec implements Serializable{
	private String id;

	/**
	 * <p>
	 * set id for clamp element optional attribute
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns id for clamp element optional attribute
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
}
