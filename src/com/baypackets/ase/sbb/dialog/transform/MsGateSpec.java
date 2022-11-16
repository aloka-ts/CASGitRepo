/**
 * MsGateSpec.java
 * 
 * This class is created for MSML Dialog Transfrom Package support for rfc 5707.<br>
 * This class will be used for gate tag under dialog transform package of msml.<br>
 * This class includes getters and setters for manipulating attributes of gate element.  
 * 
 *  
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.transform;

import java.io.Serializable;

import com.baypackets.ase.sbb.MsSendSpec;

public class MsGateSpec implements Serializable{
	public static final boolean INITIAL_PASS=true;
	public static final boolean INITIAL_HALT=false;
	public static final String EVENT_MUTE="mute";
	public static final String EVENT_UNMUTE="unmute";
	
	private String id;
	private boolean initial=true;
	private MsSendSpec sendForGate;
	
	/**
	 * This method sets id for gate element. Optional attribute.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method returns id for gate element. Optional attribute.
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * This method sets initial attribute for gate element.Default is pass.
	 * Possible values are :- true for initial=pass and false for initial=halt.
	 * @param initial the initial to set
	 */
	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	/**
	 * This method returns initial attribute for gate element.
	 * @return the initial
	 */
	public boolean isInitial() {
		return initial;
	}

	/**
	 * This method sets send tag for gate element.
	 * @param sendForGate the sendForGate to set
	 */
	public void setSendForGate(MsSendSpec sendForGate) {
		this.sendForGate = sendForGate;
	}

	/**
	 * This method returns send tag for gate element.
	 * @return the sendForGate
	 */
	public MsSendSpec getSendForGate() {
		return sendForGate;
	}
	
}
