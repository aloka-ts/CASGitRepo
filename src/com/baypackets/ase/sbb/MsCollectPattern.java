/*
 * MsCollectPattern.java
 *
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb;
import java.io.Serializable;

public class MsCollectPattern implements Serializable{
	
	public static final int ITERATE_FOREVER=Integer.MAX_VALUE; 
	//constant for format of pattern dialogic supports moml+digit
	public static final String FORMAT_MOML_DIGIT="moml+digit";
	public static final String FORMAT_MGCP="mgcp";
	public static final String FORMAT_MEGACO="megaco";
	private String digits;
	private String format;
	private int iterate;//by default 1....
	private MsSendSpec sendTag;

	public MsCollectPattern() {
		this.iterate = 1;
	}

	public MsCollectPattern(String digits, String format, int iterate,MsSendSpec sendTag) {
		this.digits = digits;
		this.format = format;
		this.iterate = iterate;
		this.sendTag = sendTag;
	}
	/**
	 * this method set digits attribute of pattern
	 * @param digits the digits to set
	 */
	public void setDigits(String digits) {
		this.digits = digits;
	}
	/**
	 * this method returns digits attribute of pattern
	 * @return the digits
	 */
	public String getDigits() {
		return digits;
	}
	/**
	 * set format for pattern ,values can be: moml+digits (dialogic supported),mgcp and megaco
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 *  Returns format for pattern
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * 
	 * @param iterate the iterate to set
	 */
	public void setIterate(int iterate) {
		if(iterate>1)
		this.iterate = iterate;
	}
	/**
	 * returns number of iterations
	 * @return the iterate
	 */
	public int getIterate() {
		return iterate;
	}
	/**
	 * 
	 * this method sets send tag values nested in pattern element (child element of dtmf element) 
	 * @param sendTag the sendTag to set
	 */
	public void setSendTag(MsSendSpec sendTag) {
		this.sendTag = sendTag;
	}
	/**
	 * 
	 * this method sets send tag values nested in pattern element (child element of dtmf element) 
	 * @param target - target attribute in sent
	 * @param event - event in sent 
	 * @param namelist - list of shadow variables "," seperated
	 * 
	 */
	public void setSendTag(String target,String event,String namelist) {
		this.sendTag = new MsSendSpec(target, event, namelist);
	}
	/**
	 * this method returns send tag values to be nested in pattern element(child element of dtmf element) 
	 * @return the sendTag
	 */
	public MsSendSpec getSendTag() {
		return sendTag;
	}
}