/*
 * MsSendSpec.java
 *
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;

public class MsSendSpec implements Serializable{

	private String target;
	private String event;
	private String namelist;
	
	public MsSendSpec(String target,String event,String namelist)
	{
		this.target=target;
		this.event=event;
		this.namelist=namelist;
	}

	/**
	 * This method sets target attribute of send element 
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * This method returns target attribute of send element 
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * This method sets target event of send element 
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * This method returns target attribute of send element 
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * This method sets namelist attribute of send element 
	 * @param namelist the namelist to set
	 */
	public void setNamelist(String namelist) {
		this.namelist = namelist;
	}

	/**
	 * <p>
	 * This method returns namelist attribute of send element 
	 * @return the namelist
	 * </p>
	 */
	public String getNamelist() {
		return namelist;
	}	
}