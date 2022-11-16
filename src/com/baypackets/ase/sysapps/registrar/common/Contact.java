/*
 * Created on Jul 4, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.sysapps.registrar.common;

/**
 * @author rajendra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Contact {
	//contact identifier
	private long contactId;
	//contains URI associated with this contact
	private String contactAddress;
	//event which caused contact state machine to go into its current state
	private String event;
	//indicates the state of the contact, either active or terminated
	private String state;
	//contains number of seconds remaininguntil binding is due to expier
	private long expires;
	//conveys amount of time this contact has been bound to the AOR
	private long durationRegistered;
	//relative priority of this contact address
	private float priority;
	//optional attribute & contains call-id of latest REGISTER request
	//used to update this contact
	private String callId;	
	//contains last CSeq value present in a REGISTER request
	private String cSeq;
	//display name
	private String displayName;
	
	

	/**
	 * @return
	 */
	public String getCallId() {
		return callId;
	}

	/**
	 * @return
	 */
	public String getContactAddress() {
		return contactAddress;
	}

	/**
	 * @return
	 */
	public long getContactId() {
		return contactId;
	}

	/**
	 * @return
	 */
	public String getCSeq() {
		return cSeq;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return
	 */
	public long getDurationRegistered() {
		return durationRegistered;
	}

	/**
	 * @return
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @return
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * @return
	 */
	public float getPriority() {
		return priority;
	}

	/**
	 * @return
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param string
	 */
	public void setCallId(String string) {
		callId = string;
	}

	/**
	 * @param string
	 */
	public void setContactAddress(String string) {
		contactAddress = string;
	}

	/**
	 * @param l
	 */
	public void setContactId(long l) {
		contactId = l;
	}

	/**
	 * @param string
	 */
	public void setCSeq(String string) {
		cSeq = string;
	}

	/**
	 * @param string
	 */
	public void setDisplayName(String string) {
		displayName = string;
	}

	/**
	 * @param l
	 */
	public void setDurationRegistered(long l) {
		durationRegistered = l;
	}

	/**
	 * @param string
	 */
	public void setEvent(String string) {
		event = string;
	}

	/**
	 * @param l
	 */
	public void setExpires(long l) {
		expires = l;
	}

	/**
	 * @param i
	 */
	public void setPriority(float i) {
		priority = i;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		state = string;
	}

}
