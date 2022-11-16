/**
 * MsGainSpec.java
 * 
 * This class is created for MSML Dialog Transfrom Package support for rfc 5707.<br>
 * This class will be used for gain tag under dialog transform package of msml.<br>
 * This class includes getters and setters for manipulating attributes of gain element.  
 * 
 *  
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.transform;
import java.io.Serializable;

import com.baypackets.ase.sbb.MsSendSpec;

public class MsGainSpec implements Serializable{
	// events for gain supported by send element
	public static final String EVENT_MUTE="mute";
	public static final String EVENT_UNMUTE="unmute";
	public static final String EVENT_RESET="reset";
	public static final String EVENT_LOUDER="louder";
	public static final String EVENT_SOFTER="softer";
	public static final String EVENT_AMT="amt";
	
	private String id; //Optional atrribute added in rfc 5707
	private int incr=3;// an increment in dB, that will be used to adjust the gain when
				// "louder" and "softer" events are received. Default is 3 dB.	
	private int amt;
	private MsSendSpec sendForGain;

	/**
	 * <p>
	 * This method sets id for gain element.Optional attribute.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method returns id for gain element.Optional attribute.
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * This method sets an increment in dB, that will be used to adjust the gain when
	 * "louder" and "softer" events are received. Default is 3 dB.	
	 * @param incr the incr to set
	 */
	public void setIncr(int incr) {
		if(incr>0)
		this.incr = incr;
	}

	/**
	 * This method returns an increment in dB, that will be used to adjust the gain when
	 * "louder" and "softer" events are received. Default is 3 dB.	
	 * @return the incr
	 */
	public int getIncr() {
		return incr;
	}

	/**
	 * This method sets amt attribute of gain element.
	 * a specific gain to apply specified in dB.
	 * Mandatory attribute. 
	 * @param amt the amt to set
	 */
	public void setAmt(int amt) {
		this.amt = amt;
	}

	/**
	 * This method returns amt attribute of gain element.
	 * a specific gain to apply specified in dB.
	 * Mandatory attribute. 
	 * @return the amt
	 */
	public int getAmt() {
		return amt;
	}

	/**
	 * This method sets send tag for gain element.
	 * @param sendForGain the sendForGain to set
	 */
	public void setSendForGain(MsSendSpec sendForGain) {
		this.sendForGain = sendForGain;
	}

	/**
	 * This method returns send tag object for gain element.
	 * @return the sendForGain
	 */
	public MsSendSpec getSendForGain() {
		return sendForGain;
	}
	
	
}
