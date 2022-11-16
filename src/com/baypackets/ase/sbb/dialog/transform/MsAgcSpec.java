/**
 * MsAgcSpec.java
 * 
 * This class is created for MSML Dialog Transfrom Package support for rfc 5707.<br>
 * This class will be used for agc tag under dialog transform package of msml.<br>
 * This class includes getters and setters for manipulating attributes of agc element.  
 * 
 *  
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.transform;

import java.io.Serializable;

import com.baypackets.ase.sbb.MsSendSpec;

public class MsAgcSpec implements Serializable{
	private String id;
	private int tgtlvl;
	private int maxGain=10;
	private MsSendSpec sendForAgc;
	public static final String EVENT_MUTE="mute";
	public static final String EVENT_UNMUTE="unmute";
	
	/**
	 * This method sets id for agc element. Optional attribute.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns id for agc element optional attribute
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * This method sets target level for automatic gain control element.
	 * Mandatory. Valid Range is from -40dBm0 to 0 dBm0
	 * @param tgtlv the tgtlv to set
	 */
	public void setTargetLevel(int tgtlv) {
		if(tgtlv<=0 && tgtlv>=-40)
		this.tgtlvl = tgtlv;
	}
	
	/**
	 * This method returns target level in dBm0 for automatic gain control element.
	 * Mandatory. Valid Range is from -40dBm0 to 0 dBm0
	 * @return the tgtlv
	 */
	public int getTargetLevel() {
		return tgtlvl;
	}
	
	/**
	 * This method sets maxgain in dBm0 for automatic gain control element.
	 * Valid Range is from 0dBm0 to 40 dBm0.Default value is 10dBm0
	 * @param maxgain the maxgain to set
	 */
	public void setMaxGain(int maxgain) {
		if(maxgain<=40 && maxgain>=0)
		this.maxGain = maxgain;
	}
	
	/**
	 * This method returns maxgain in dBm0 for automatic gain control  element.
	 * @return the maxgain
	 */
	public int getMaxGain() {
		return maxGain;
	}

	/**
	 * This method sets send tag for agc element.
	 * @param sendForAgc the sendForAgc to set
	 */
	public void setSendForAgc(MsSendSpec sendForAgc) {
		this.sendForAgc = sendForAgc;
	}

	/**
	 * This method returns send tag for agc element.
	 * @return the sendForAgc
	 */
	public MsSendSpec getSendForAgc() {
		return sendForAgc;
	}
}
